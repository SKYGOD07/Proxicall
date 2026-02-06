'use client';

import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Activity, Radio, Mic, Phone, ArrowLeft, Smartphone } from 'lucide-react';
import Link from 'next/link';
import { useAuth } from './AuthProvider';
import { useRouter } from 'next/navigation';
import LoadingScreen from './LoadingScreen';
import BluetoothControl from './dashboard/BluetoothControl';
import DeviceManager from './dashboard/DeviceManager';
import CallLogs from './dashboard/CallLogs';
import AIVerification from './dashboard/AIVerification';
import ContactManager from './dashboard/ContactManager';

export default function Dashboard() {
    const { user, logout, loading } = useAuth();
    const router = useRouter();
    const [isLoading, setIsLoading] = useState(true);
    const [activeTab, setActiveTab] = useState<'bluetooth' | 'devices' | 'logs' | 'ai' | 'contacts'>('bluetooth');

    useEffect(() => {
        if (!loading && !user) {
            router.push('/login');
        }
    }, [user, loading, router]);

    if (loading) return (
        <div className="flex items-center justify-center min-h-screen bg-black text-white">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-cyan-400"></div>
        </div>
    );

    if (!user) return null;

    const navItems = [
        { id: 'bluetooth', label: 'Connection', icon: Radio },
        { id: 'devices', label: 'Auth Check', icon: Smartphone },
        { id: 'logs', label: 'Activity Logs', icon: Activity },
        { id: 'ai', label: 'Brain Verify', icon: Mic },
        { id: 'contacts', label: 'Contacts', icon: Phone },
    ];

    return (
        <div className="min-h-screen bg-black text-white font-sans relative overflow-x-hidden flex flex-col">
            {/* Background Gradients */}
            <div className="fixed inset-0 bg-[radial-gradient(circle_at_top_right,_var(--tw-gradient-stops))] from-indigo-900/20 via-black to-black -z-20" />
            <div className="fixed inset-0 bg-[radial-gradient(circle_at_bottom_left,_var(--tw-gradient-stops))] from-cyan-900/10 via-black to-black -z-20" />

            <AnimatePresence>
                {isLoading && (
                    <LoadingScreen onComplete={() => setIsLoading(false)} />
                )}
            </AnimatePresence>

            {/* Header */}
            <header className="px-6 py-4 flex items-center justify-between border-b border-white/10 bg-black/50 backdrop-blur-md sticky top-0 z-50">
                <div className="flex items-center gap-4">
                    <Link href="/">
                        <button className="p-2 rounded-full hover:bg-white/10 transition-colors text-slate-400 hover:text-white">
                            <ArrowLeft size={20} />
                        </button>
                    </Link>
                    <h1 className="text-xl font-bold bg-gradient-to-r from-cyan-400 to-indigo-500 bg-clip-text text-transparent flex items-center gap-2">
                        <Activity size={20} className="text-cyan-400" />
                        ProxiCall Agent
                    </h1>
                </div>

                {/* Desktop Navigation */}
                <nav className="hidden md:flex items-center gap-1 bg-white/5 rounded-full p-1 border border-white/5">
                    {navItems.map((item) => (
                        <button
                            key={item.id}
                            onClick={() => setActiveTab(item.id as any)}
                            className={`px-4 py-2 rounded-full text-sm font-medium transition-all flex items-center gap-2
                                ${activeTab === item.id
                                    ? 'bg-slate-800 text-white shadow-lg'
                                    : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'}
                            `}
                        >
                            <item.icon size={16} />
                            {item.label}
                        </button>
                    ))}
                </nav>

                <div className="flex items-center gap-4">
                    <div className="flex items-center gap-2 text-xs font-mono text-slate-500 hidden sm:flex">
                        <div className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
                        <span>ONLINE</span>
                    </div>

                    <div className="flex items-center gap-3 pl-4 border-l border-white/10">
                        {user.photoURL && (
                            <img src={user.photoURL} alt="User" className="w-8 h-8 rounded-full border border-white/20" />
                        )}
                        <button
                            onClick={logout}
                            className="text-xs text-red-400 hover:text-red-300 transition-colors font-semibold"
                        >
                            Sign Out
                        </button>
                    </div>
                </div>
            </header>

            {/* Mobile Navigation (Bottom) */}
            <nav className="md:hidden fixed bottom-0 left-0 right-0 bg-black/90 backdrop-blur-lg border-t border-white/10 p-2 flex justify-around z-50">
                {navItems.map((item) => (
                    <button
                        key={item.id}
                        onClick={() => setActiveTab(item.id as any)}
                        className={`p-3 rounded-xl flex flex-col items-center gap-1 transition-colors
                            ${activeTab === item.id ? 'text-cyan-400' : 'text-slate-500'}
                        `}
                    >
                        <item.icon size={20} />
                        <span className="text-[10px] uppercase font-bold">{item.label}</span>
                    </button>
                ))}
            </nav>

            {/* Main Content Area */}
            <main className="flex-1 max-w-7xl mx-auto w-full p-4 md:p-8 mb-20 md:mb-0">
                <motion.div
                    key={activeTab}
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -10 }}
                    transition={{ duration: 0.3 }}
                    className="h-full"
                >
                    {activeTab === 'bluetooth' && <BluetoothControl />}
                    {activeTab === 'devices' && <DeviceManager />}
                    {activeTab === 'logs' && <CallLogs />}
                    {activeTab === 'ai' && <AIVerification />}
                    {activeTab === 'contacts' && <ContactManager />}
                </motion.div>
            </main>
        </div>
    );
}
