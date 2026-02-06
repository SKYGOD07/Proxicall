'use client';

import { motion } from 'framer-motion';
import { Bluetooth, ShieldCheck, Wifi } from 'lucide-react';
import { useState } from 'react';

export default function BluetoothControl() {
    const [isActive, setIsActive] = useState(false);

    return (
        <div className="flex flex-col items-center justify-center h-full min-h-[500px] w-full relative overflow-hidden">
            {/* Background Pulse Effect */}
            {isActive && (
                <>
                    <motion.div
                        initial={{ opacity: 0.5, scale: 0.8 }}
                        animate={{ opacity: 0, scale: 2 }}
                        transition={{ repeat: Infinity, duration: 2, ease: "easeOut" }}
                        className="absolute w-64 h-64 rounded-full bg-cyan-500/20 z-0"
                    />
                    <motion.div
                        initial={{ opacity: 0.5, scale: 0.8 }}
                        animate={{ opacity: 0, scale: 2 }}
                        transition={{ repeat: Infinity, duration: 2, ease: "easeOut", delay: 0.5 }}
                        className="absolute w-64 h-64 rounded-full bg-cyan-500/10 z-0"
                    />
                </>
            )}

            <div className="z-10 text-center space-y-8">
                <div className="space-y-2">
                    <h2 className="text-3xl font-bold text-white">Bluetooth Status</h2>
                    <p className={`text-sm font-medium tracking-wider uppercase ${isActive ? 'text-cyan-400' : 'text-slate-500'}`}>
                        {isActive ? "Scanning for Signals" : "Radio Silent"}
                    </p>
                </div>

                {/* Big Toggle Button */}
                <motion.button
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    onClick={() => setIsActive(!isActive)}
                    className={`w-48 h-48 rounded-full border-4 flex items-center justify-center transition-all duration-500 relative
                        ${isActive
                            ? 'bg-cyan-950/30 border-cyan-500 shadow-[0_0_50px_rgba(6,182,212,0.5)]'
                            : 'bg-slate-900 border-slate-700 shadow-none'
                        }
                    `}
                >
                    <Bluetooth
                        size={64}
                        className={`transition-colors duration-500 ${isActive ? 'text-cyan-400' : 'text-slate-600'}`}
                    />
                    {/* Power Indicator Ring */}
                    {isActive && (
                        <svg className="absolute inset-0 w-full h-full -rotate-90" viewBox="0 0 100 100">
                            <circle
                                cx="50" cy="50" r="46"
                                fill="none" stroke="currentColor" strokeWidth="2"
                                className="text-cyan-500 animate-[spin_3s_linear_infinite]"
                                strokeDasharray="20 10"
                            />
                        </svg>
                    )}
                </motion.button>

                <p className="text-slate-400 max-w-xs mx-auto text-sm">
                    {isActive
                        ? "Agent is actively scanning for your trusted device beacon."
                        : "Tap to activate Bluetooth scanning and sync with your device."}
                </p>
            </div>
        </div>
    );
}
