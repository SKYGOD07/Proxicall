'use client';

import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Activity, Radio, Mic, Phone, CheckCircle2, AlertTriangle, ArrowLeft } from 'lucide-react';
import Link from 'next/link';
import RadarAnimation from './RadarAnimation';
import LoadingScreen from './LoadingScreen';
import ConnectionBreadcrumb from './ConnectionBreadcrumb';

export default function Dashboard() {
    const [isLoading, setIsLoading] = useState(true);
    const [isConnected, setIsConnected] = useState(true);
    const [autoReplyEnabled, setAutoReplyEnabled] = useState(true);
    const [whisperEnabled, setWhisperEnabled] = useState(false);

    // Mock Data
    const activities = [
        { id: 1, type: 'auto-reply', message: "Auto-replied to Mom: 'In a meeting'", time: '2m ago' },
        { id: 2, type: 'whisper', message: "Whispered: 'Call from Boss - Priority High'", time: '15m ago' },
        { id: 3, type: 'connect', message: "Reconnected to Pixel 8 Pro", time: '1h ago' },
    ];

    // Determine current step for breadcrumb
    const getBreadcrumbStatus = () => {
        if (!isConnected) return 'scanning';
        // In a real app, "authenticated" would be a distinct state
        // For demo, we'll assume connected implies nearly authenticated
        return 'connected';
    };

    return (
        <div className="min-h-screen bg-black text-white p-6 font-sans relative overflow-x-hidden">
            {/* Background Gradients (Matches Landing Page) */}
            <div className="fixed inset-0 bg-[radial-gradient(circle_at_top_right,_var(--tw-gradient-stops))] from-indigo-900/20 via-black to-black -z-20" />
            <div className="fixed inset-0 bg-[radial-gradient(circle_at_bottom_left,_var(--tw-gradient-stops))] from-cyan-900/10 via-black to-black -z-20" />

            <AnimatePresence>
                {isLoading && (
                    <LoadingScreen onComplete={() => setIsLoading(false)} />
                )}
            </AnimatePresence>

            {/* Header */}
            <header className="flex items-center justify-between mb-8 pb-4 border-b border-white/10">
                <div className="flex items-center gap-4">
                    <Link href="/">
                        <button className="p-2 rounded-full hover:bg-white/10 transition-colors text-slate-400 hover:text-white">
                            <ArrowLeft size={20} />
                        </button>
                    </Link>
                    <div>
                        <h1 className="text-2xl font-bold bg-gradient-to-r from-cyan-400 to-indigo-500 bg-clip-text text-transparent flex items-center gap-2">
                            <Activity size={24} className="text-cyan-400" />
                            ProxiCall Agent Active
                        </h1>
                        <div className="flex items-center gap-2 text-xs font-mono text-slate-500 mt-1">
                            <span>V 1.0.0</span>
                            <div className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
                            <span>SYSTEM ONLINE</span>
                        </div>
                    </div>
                </div>
            </header>

            <main className="max-w-6xl mx-auto">
                {/* Breadcrumb Section */}
                <div className="mb-12 text-center">
                    <ConnectionBreadcrumb status={getBreadcrumbStatus()} />
                    <p className="text-slate-500 text-sm mt-4 max-w-lg mx-auto bg-slate-900/50 py-2 px-4 rounded-full border border-slate-800">
                        {isConnected ?
                            "System Ready. Waiting for incoming calls..." :
                            "Action Required: Enable Bluetooth on your Android device and pair with the Agent."
                        }
                    </p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                    {/* Left Column: Status & Controls */}
                    <div className="space-y-6">

                        {/* Status Card */}
                        <div className={`p-8 rounded-3xl border backdrop-blur-md transition-all duration-500 relative overflow-hidden group
                            ${isConnected
                                ? 'bg-emerald-950/10 border-emerald-500/30 shadow-[0_0_30px_rgba(16,185,129,0.1)]'
                                : 'bg-amber-950/10 border-amber-500/30 shadow-[0_0_30px_rgba(245,158,11,0.1)]'
                            }
                        `}>
                            <div className={`absolute inset-0 opacity-10 ${isConnected ? 'bg-emerald-500' : 'bg-amber-500'} blur-3xl -z-10`} />

                            <div className="flex items-center justify-between mb-6">
                                <span className="text-slate-400 text-xs uppercase tracking-widest font-bold">Phone Status Link</span>
                                {isConnected ? <CheckCircle2 className="text-emerald-400" /> : <AlertTriangle className="text-amber-400" />}
                            </div>

                            <div className="flex items-center gap-6">
                                {/* Minimized Radar */}
                                <div className="w-20 h-20 relative">
                                    <div className={`absolute inset-0 rounded-full border-2 ${isConnected ? 'border-emerald-500/30' : 'border-amber-500/30'} animate-ping`} />
                                    <div className={`absolute inset-4 rounded-full ${isConnected ? 'bg-emerald-500' : 'bg-amber-500'} opacity-20`} />
                                    <Radio size={32} className={`absolute inset-0 m-auto ${isConnected ? 'text-emerald-400' : 'text-amber-400'}`} />
                                </div>
                                <div>
                                    <h2 className={`text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r ${isConnected ? 'from-emerald-300 to-emerald-500' : 'from-amber-300 to-amber-500'}`}>
                                        {isConnected ? "Connected" : "Scanning..."}
                                    </h2>
                                    <p className="text-slate-400 text-sm mt-1">
                                        Signal: <span className="font-mono text-white">{isConnected ? "-42 dBm" : "-85 dBm"}</span>
                                    </p>
                                </div>
                            </div>
                            <button
                                onClick={() => setIsConnected(!isConnected)}
                                className="mt-6 text-xs text-slate-500 hover:text-white transition-colors underline decoration-slate-700 hover:decoration-white"
                            >
                                (Simulate Connection Toggle)
                            </button>
                        </div>

                        {/* Controls */}
                        <div className="space-y-4">
                            {/* Scenario A */}
                            <div className="flex items-center justify-between p-6 bg-white/5 rounded-2xl border border-white/10 backdrop-blur-md hover:border-cyan-500/30 transition-colors">
                                <div className="flex items-center gap-4">
                                    <div className={`p-4 rounded-xl ${autoReplyEnabled ? 'bg-cyan-500/20 text-cyan-400' : 'bg-slate-800/50 text-slate-600'}`}>
                                        <Phone size={24} />
                                    </div>
                                    <div>
                                        <h3 className="font-bold text-lg text-slate-200">Auto-Reply Agent</h3>
                                        <p className="text-xs text-slate-500 uppercase tracking-wide">Autonomous SMS Response</p>
                                    </div>
                                </div>
                                <button
                                    onClick={() => setAutoReplyEnabled(!autoReplyEnabled)}
                                    className={`w-14 h-8 rounded-full p-1 transition-colors ${autoReplyEnabled ? 'bg-cyan-600 shadow-[0_0_15px_rgba(8,145,178,0.4)]' : 'bg-slate-700'}`}
                                >
                                    <div className={`w-6 h-6 bg-white rounded-full shadow-md transition-transform ${autoReplyEnabled ? 'translate-x-6' : 'translate-x-0'}`} />
                                </button>
                            </div>

                            {/* Scenario B */}
                            <div className="flex items-center justify-between p-6 bg-white/5 rounded-2xl border border-white/10 backdrop-blur-md hover:border-indigo-500/30 transition-colors">
                                <div className="flex items-center gap-4">
                                    <div className={`p-4 rounded-xl ${whisperEnabled ? 'bg-indigo-500/20 text-indigo-400' : 'bg-slate-800/50 text-slate-600'}`}>
                                        <Mic size={24} />
                                    </div>
                                    <div>
                                        <h3 className="font-bold text-lg text-slate-200">Whisper Mode</h3>
                                        <p className="text-xs text-slate-500 uppercase tracking-wide">Gemini Live Voice Intercept</p>
                                    </div>
                                </div>
                                <button
                                    onClick={() => setWhisperEnabled(!whisperEnabled)}
                                    className={`w-14 h-8 rounded-full p-1 transition-colors ${whisperEnabled ? 'bg-indigo-600 shadow-[0_0_15px_rgba(79,70,229,0.4)]' : 'bg-slate-700'}`}
                                >
                                    <div className={`w-6 h-6 bg-white rounded-full shadow-md transition-transform ${whisperEnabled ? 'translate-x-6' : 'translate-x-0'}`} />
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Right Column: Activity Log */}
                    <div className="bg-white/5 rounded-3xl border border-white/10 backdrop-blur-md p-8 h-fit">
                        <h3 className="text-xl font-bold mb-8 flex items-center justify-between">
                            Live Activity Stream
                            <span className="text-xs font-bold text-emerald-400 bg-emerald-950/30 px-3 py-1 rounded-full border border-emerald-900/50 animate-pulse">LIVE</span>
                        </h3>
                        <div className="space-y-8 relative">
                            <div className="absolute left-[19px] top-2 bottom-2 w-0.5 bg-gradient-to-b from-slate-700 to-transparent" />

                            {activities.map((item, index) => (
                                <motion.div
                                    key={item.id}
                                    initial={{ opacity: 0, x: -20 }}
                                    animate={{ opacity: 1, x: 0 }}
                                    transition={{ delay: index * 0.1 }}
                                    className="relative pl-12 group"
                                >
                                    <div className={`absolute left-0 top-1 w-10 h-10 rounded-full border-4 border-black flex items-center justify-center z-10 shadow-lg
                                        ${item.type === 'auto-reply' ? 'bg-cyan-500' : item.type === 'whisper' ? 'bg-indigo-500' : 'bg-emerald-500'}
                                    `}>
                                        {item.type === 'auto-reply' ? <Phone size={16} className="text-white" /> :
                                            item.type === 'whisper' ? <Mic size={16} className="text-white" /> :
                                                <Activity size={16} className="text-white" />}
                                    </div>
                                    <div className="group-hover:bg-white/5 p-4 rounded-xl transition-colors -ml-4 pl-16">
                                        <p className="text-slate-200 font-medium text-base mb-1">{item.message}</p>
                                        <div className="flex items-center gap-2 text-xs text-slate-500">
                                            <span>{item.time}</span>
                                            <span>•</span>
                                            <span className="uppercase tracking-wider">Success</span>
                                        </div>
                                    </div>
                                </motion.div>
                            ))}
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}
