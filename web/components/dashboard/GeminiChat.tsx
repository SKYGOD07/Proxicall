'use client';

import { useState, useRef, useEffect } from 'react';
import { Send, Mic, MicOff, Battery, AlertTriangle, MessageSquare } from 'lucide-react';
import { useAuth } from '../AuthProvider';
import { useToast } from '../ToastProvider';
import { GoogleGenerativeAI } from "@google/generative-ai";

// Quick interface for chat messages
interface Message {
    id: string;
    role: 'user' | 'model';
    text: string;
    timestamp: Date;
}

export default function GeminiChat() {
    const { user } = useAuth();
    const { showToast } = useToast();
    const [input, setInput] = useState('');
    const [messages, setMessages] = useState<Message[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isListening, setIsListening] = useState(false);
    const [apiKey, setApiKey] = useState('');
    const [showKeyInput, setShowKeyInput] = useState(false);

    // Auto-scroll ref
    const messagesEndRef = useRef<HTMLDivElement>(null);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    // Check for env key
    // Hardcoded API Key as requested by user
    const API_KEY = "AIzaSyDE9DQWihqJoN25jUVBLwBLLyAAwIVoYE0";

    useEffect(() => {
        setApiKey(API_KEY);
    }, []);

    const handleSend = async () => {
        if (!input.trim() || !apiKey) return;

        const userMsg: Message = {
            id: Date.now().toString(),
            role: 'user',
            text: input,
            timestamp: new Date()
        };
        setMessages(prev => [...prev, userMsg]);
        setInput('');
        setIsLoading(true);

        try {
            const genAI = new GoogleGenerativeAI(apiKey);
            const model = genAI.getGenerativeModel({ model: "gemini-pro" });

            const result = await model.generateContent(input);
            const response = await result.response;
            const text = response.text();

            const aiMsg: Message = {
                id: (Date.now() + 1).toString(),
                role: 'model',
                text: text,
                timestamp: new Date()
            };
            setMessages(prev => [...prev, aiMsg]);

            // Text to Speech
            speak(text);

        } catch (error) {
            console.error("Gemini Error:", error);
            const errorMsg: Message = {
                id: (Date.now() + 1).toString(),
                role: 'model',
                text: "I'm having trouble connecting to the neural network. Please check your API key.",
                timestamp: new Date()
            };
            setMessages(prev => [...prev, errorMsg]);
        } finally {
            setIsLoading(false);
        }
    };

    const toggleListening = () => {
        if (isListening) {
            stopListening();
        } else {
            startListening();
        }
    };

    const startListening = () => {
        if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
            const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
            const recognition = new SpeechRecognition();
            recognition.continuous = false;
            recognition.interimResults = false;
            recognition.lang = 'en-US';

            recognition.onstart = () => setIsListening(true);
            recognition.onend = () => setIsListening(false);
            recognition.onresult = (event: any) => {
                const transcript = event.results[0][0].transcript;
                setInput(transcript);
                // Optional: Auto-send
                // handleSend();
            };
            recognition.start();
        } else {
            showToast("Voice input not supported in this browser.", "error");
        }
    };

    const stopListening = () => {
        setIsListening(false);
        // Logic to stop if reference held, but usually single shot is fine for now
    };

    const speak = (text: string) => {
        if ('speechSynthesis' in window) {
            const utterance = new SpeechSynthesisUtterance(text);
            window.speechSynthesis.speak(utterance);
        }
    };

    return (
        <div className="flex flex-col h-full bg-slate-900/50 rounded-2xl border border-white/10 overflow-hidden relative">
            {/* Header */}
            <div className="p-4 border-b border-white/10 bg-black/20 flex justify-between items-center">
                <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-indigo-500 to-cyan-500 flex items-center justify-center shadow-lg shadow-cyan-500/20">
                        <MessageSquare size={16} className="text-white" />
                    </div>
                    <div>
                        <h3 className="font-bold text-white">Gemini Assistant</h3>
                        <p className="text-[10px] text-slate-400 flex items-center gap-1">
                            <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                            Neural Link Active
                        </p>
                    </div>
                </div>
            </div>

            {/* API Key Input (Conditional) */}


            {/* Messages Area */}
            <div className="flex-1 overflow-y-auto p-4 space-y-4">
                {messages.length === 0 && (
                    <div className="flex flex-col items-center justify-center h-full text-slate-500 opacity-50">
                        <MessageSquare size={48} className="mb-2" />
                        <p>Start a conversation...</p>
                    </div>
                )}
                {messages.map((msg) => (
                    <div
                        key={msg.id}
                        className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}
                    >
                        <div className={`max-w-[80%] p-3 rounded-2xl text-sm ${msg.role === 'user'
                            ? 'bg-indigo-600 text-white rounded-tr-none'
                            : 'bg-slate-800 text-slate-200 rounded-tl-none border border-white/5'
                            }`}>
                            {msg.text}
                        </div>
                    </div>
                ))}
                {isLoading && (
                    <div className="flex justify-start">
                        <div className="bg-slate-800 p-3 rounded-2xl rounded-tl-none border border-white/5 flex gap-1">
                            <span className="w-2 h-2 bg-slate-500 rounded-full animate-bounce"></span>
                            <span className="w-2 h-2 bg-slate-500 rounded-full animate-bounce delay-75"></span>
                            <span className="w-2 h-2 bg-slate-500 rounded-full animate-bounce delay-150"></span>
                        </div>
                    </div>
                )}
                <div ref={messagesEndRef} />
            </div>

            {/* Input Area */}
            <div className="p-4 bg-black/20 border-t border-white/10">
                <div className="flex items-center gap-2 bg-slate-800/50 rounded-xl p-2 border border-white/5 focus-within:border-cyan-500/50 transition-colors">
                    <button
                        onClick={toggleListening}
                        className={`p-2 rounded-lg transition-all ${isListening ? 'bg-red-500/20 text-red-400 animate-pulse' : 'hover:bg-white/5 text-slate-400'}`}
                    >
                        {isListening ? <MicOff size={18} /> : <Mic size={18} />}
                    </button>
                    <input
                        type="text"
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                        placeholder="Type a message..."
                        className="flex-1 bg-transparent text-sm text-white placeholder:text-slate-500 focus:outline-none"
                    />
                    <button
                        onClick={handleSend}
                        disabled={!input.trim() || isLoading}
                        className="p-2 bg-cyan-600 hover:bg-cyan-500 text-white rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        <Send size={18} />
                    </button>
                </div>
            </div>

            {/* Context Awareness Notice */}
            <div className="absolute top-4 right-4 hidden md:block">
                {/* Could indicate app connectivity here */}
            </div>
        </div>
    );
}
