'use client';

import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Activity, Radio, Mic, Phone, CheckCircle2, AlertTriangle } from 'lucide-react';
import RadarAnimation from './RadarAnimation';

export default function Dashboard() {
    const [isConnected, setIsConnected] = useState(true);
    const [autoReplyEnabled, setAutoReplyEnabled] = useState(true);
    const [whisperEnabled, setWhisperEnabled] = useState(false);

    // Mock Data
    const activities = [
        { id: 1, type: 'auto-reply', message: "Auto-replied to Mom: 'In a meeting'", time: '2m ago' },
        { id: 2, type: 'whisper', message: "Whispered: 'Call from Boss - Priority High'", time: '15m ago' },
        { id: 3, type: 'connect', message: "Reconnected to Pixel 8 Pro", time: '1h ago' },
    ];

    return (
        <div className="min-h-screen bg-black text-slate-200 p-6 font-sans">
            {/* Header */}
            <header className="flex items-center justify-between mb-8 border-b border-slate-800 pb-4">
                <h1 className="text-2xl font-bold bg-gradient-to-r from-cyan-400 to-indigo-500 bg-clip-text text-transparent flex items-center gap-2">
                    <Activity size={24} className="text-cyan-400" />
                    ProxiCall Agent Active
                </h1>
                <div className="flex items-center gap-2 text-xs font-mono text-slate-500">
                    <span>V 1.0.0</span>
                    <div className="w-2 h-2 rounded-full bg-green-500 animate-pulse" />
                </div>
            </header>

            <main className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-5xl mx-auto">

                {/* Left Column: Status & Controls */}
                <div className="space-y-6">

                    {/* Status Card */}
                    <div className={`p-6 rounded-2xl border ${isConnected ? 'bg-slate-900/50 border-green-900/50' : 'bg-amber-900/10 border-amber-900/50'} transition-colors duration-500`}>
                        <div className="flex items-center justify-between mb-4">
                            <span className="text-slate-400 text-sm uppercase tracking-wider font-semibold">Phone Status</span>
                            {isConnected ? <CheckCircle2 className="text-green-500" /> : <AlertTriangle className="text-amber-500" />}
                        </div>
                        <div className="flex items-center gap-4">
                            {/* Embed Radar Animation Scaled Down */}
                            <div className="w-16 h-16 relative">
                                <div className={`absolute inset-0 rounded-full border-2 ${isConnected ? 'border-green-500/30' : 'border-amber-500/30'} animate-ping`} />
                                <div className={`absolute inset-2 rounded-full ${isConnected ? 'bg-green-500' : 'bg-amber-500'} opacity-20`} />
                                <Radio size={32} className={`absolute inset-0 m-auto ${isConnected ? 'text-green-400' : 'text-amber-400'}`} />
                            </div>
                            <div>
                                <h2 className={`text-2xl font-bold ${isConnected ? 'text-green-400' : 'text-amber-400'}`}>
                                    {isConnected ? "Connected" : "Away"}
                                </h2>
                                <p className="text-slate-500 text-sm">Signal Strength: {isConnected ? "-42 dBm (Strong)" : "-85 dBm (Weak)"}</p>
                            </div>
                        </div>
                        <button
                            onClick={() => setIsConnected(!isConnected)}
                            className="mt-4 text-xs text-slate-600 hover:text-slate-400 underline"
                        >
                            (Toggle Sim)
                        </button>
                    </div>

                    {/* Controls */}
                    <div className="space-y-4">
                        {/* Scenario A */}
                        <div className="flex items-center justify-between p-4 bg-slate-900 rounded-xl border border-slate-800">
                            <div className="flex items-center gap-4">
                                <div className={`p-3 rounded-lg ${autoReplyEnabled ? 'bg-cyan-900/30 text-cyan-400' : 'bg-slate-800 text-slate-600'}`}>
                                    <Phone size={20} />
                                </div>
                                <div>
                                    <h3 className="font-semibold text-slate-200">Auto-Reply</h3>
                                    <p className="text-xs text-slate-500">Scenario A: Auto-respond when away</p>
                                </div>
                            </div>
                            <button
                                onClick={() => setAutoReplyEnabled(!autoReplyEnabled)}
                                className={`w-12 h-6 rounded-full p-1 transition-colors ${autoReplyEnabled ? 'bg-cyan-600' : 'bg-slate-700'}`}
                            >
                                <div className={`w-4 h-4 bg-white rounded-full transition-transform ${autoReplyEnabled ? 'translate-x-6' : 'translate-x-0'}`} />
                            </button>
                        </div>

                        {/* Scenario B */}
                        <div className="flex items-center justify-between p-4 bg-slate-900 rounded-xl border border-slate-800">
                            <div className="flex items-center gap-4">
                                <div className={`p-3 rounded-lg ${whisperEnabled ? 'bg-indigo-900/30 text-indigo-400' : 'bg-slate-800 text-slate-600'}`}>
                                    <Mic size={20} />
                                </div>
                                <div>
                                    <h3 className="font-semibold text-slate-200">Whisper Agent</h3>
                                    <p className="text-xs text-slate-500">Scenario B: Gemini Live when near</p>
                                </div>
                            </div>
                            <button
                                onClick={() => setWhisperEnabled(!whisperEnabled)}
                                className={`w-12 h-6 rounded-full p-1 transition-colors ${whisperEnabled ? 'bg-indigo-600' : 'bg-slate-700'}`}
                            >
                                <div className={`w-4 h-4 bg-white rounded-full transition-transform ${whisperEnabled ? 'translate-x-6' : 'translate-x-0'}`} />
                            </button>
                        </div>
                    </div>
                </div>

                {/* Right Column: Activity Log */}
                <div className="bg-slate-900 rounded-2xl border border-slate-800 p-6">
                    <h3 className="text-lg font-semibold mb-6 flex items-center gap-2">
                        Recent Activity
                        <span className="text-xs font-normal text-slate-500 bg-slate-800 px-2 py-0.5 rounded-full">{activities.length} new</span>
                    </h3>
                    <div className="space-y-6 relative">
                        <div className="absolute left-[19px] top-2 bottom-2 w-0.5 bg-slate-800" />

                        {activities.map((item) => (
                            <motion.div
                                key={item.id}
                                initial={{ opacity: 0, x: -20 }}
                                animate={{ opacity: 1, x: 0 }}
                                className="relative pl-10"
                            >
                                <div className={`absolute left-0 top-1 w-10 h-10 rounded-full border-4 border-slate-900 flex items-center justify-center z-10
                  ${item.type === 'auto-reply' ? 'bg-cyan-500' : item.type === 'whisper' ? 'bg-indigo-500' : 'bg-green-500'}
                `}>
                                    {item.type === 'auto-reply' ? <Phone size={14} className="text-white" /> :
                                        item.type === 'whisper' ? <Mic size={14} className="text-white" /> :
                                            <Activity size={14} className="text-white" />}
                                </div>
                                <div className="bg-slate-800/50 p-4 rounded-xl border border-slate-700/50 hover:border-slate-600 transition-colors">
                                    <p className="text-slate-300 font-medium text-sm">{item.message}</p>
                                    <p className="text-slate-500 text-xs mt-1">{item.time}</p>
                                </div>
                            </motion.div>
                        ))}
                    </div>
                </div>

            </main>
        </div>
    );
}
