'use client';

import { motion } from 'framer-motion';
import { useEffect, useState } from 'react';
import { Scan, Radio, Target } from 'lucide-react';

export default function RadarAnimation() {
    const [foundSignal, setFoundSignal] = useState(false);

    // Simulate finding a signal
    useEffect(() => {
        const timer = setTimeout(() => {
            setFoundSignal(true);
        }, 3500);
        return () => clearTimeout(timer);
    }, []);

    // Animation variants
    const scanVariant = {
        animate: {
            rotate: 360,
            transition: {
                duration: 3,
                ease: "linear",
                repeat: Infinity
            }
        }
    };

    const pulseVariant = {
        animate: {
            scale: [1, 1.4, 1.8],
            opacity: [0.5, 0.2, 0],
            transition: {
                duration: 2,
                repeat: Infinity,
                ease: "easeOut"
            }
        }
    };

    return (
        <div className="relative flex flex-col items-center justify-center p-12 bg-gray-950 min-h-[400px] w-full max-w-md mx-auto rounded-3xl overflow-hidden border border-slate-800 shadow-2xl">
            {/* Ambient Background Glow */}
            <div className="absolute inset-0 bg-[radial-gradient(circle_at_center,_var(--tw-gradient-stops))] from-cyan-900/20 via-slate-950 to-slate-950" />

            {/* Radar Container */}
            <div className="relative w-64 h-64 flex items-center justify-center">
                {/* Grid Lines */}
                <div className="absolute inset-0 border border-slate-700/50 rounded-full" />
                <div className="absolute inset-4 border border-slate-800/50 rounded-full" />
                <div className="absolute inset-16 border border-slate-800/50 rounded-full" />

                {/* Crosshairs */}
                <div className="absolute w-full h-[1px] bg-slate-800/50" />
                <div className="absolute h-full w-[1px] bg-slate-800/50" />

                {/* Rotating Scanner Beam */}
                <motion.div
                    className="absolute inset-0 rounded-full"
                    variants={scanVariant}
                    animate="animate"
                >
                    <div className="w-full h-full bg-[conic-gradient(from_0deg,transparent_0deg,transparent_270deg,rgba(6,182,212,0.1)_320deg,rgba(6,182,212,0.4)_360deg)] rounded-full drop-shadow-[0_0_15px_rgba(6,182,212,0.5)]" />
                </motion.div>

                {/* Pulsing Signal Ripples (Active when scanning) */}
                {!foundSignal && (
                    <>
                        <motion.div
                            className="absolute inset-0 border border-cyan-500/30 rounded-full"
                            variants={pulseVariant}
                            animate="animate"
                        />
                        <motion.div
                            className="absolute inset-0 border border-cyan-500/30 rounded-full"
                            variants={pulseVariant}
                            animate="animate"
                            transition={{ delay: 0.6 }}
                        />
                    </>
                )}

                {/* Detected Signal Dot */}
                {foundSignal && (
                    <motion.div
                        initial={{ scale: 0, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        className="absolute top-12 right-12 w-4 h-4 bg-red-500 rounded-full shadow-[0_0_10px_#ef4444]"
                    >
                        <motion.div
                            animate={{ scale: [1, 2], opacity: [0.5, 0] }}
                            transition={{ duration: 1, repeat: Infinity }}
                            className="absolute inset-0 bg-red-500 rounded-full"
                        />
                    </motion.div>
                )}

                {/* Center Core */}
                <div className="absolute w-3 h-3 bg-cyan-400 rounded-full shadow-[0_0_10px_#22d3ee] z-10" />
            </div>

            {/* Status Panel */}
            <div className="relative z-10 mt-12 w-full space-y-2">
                <div className="flex items-center justify-between text-xs uppercase tracking-widest text-slate-500 font-semibold">
                    <span className="flex items-center gap-2">
                        <Radio size={14} className={!foundSignal ? "animate-pulse text-cyan-500" : "text-slate-600"} />
                        Status
                    </span>
                    <span className={foundSignal ? "text-emerald-400 drop-shadow-md" : "text-cyan-400/80 animate-pulse"}>
                        {foundSignal ? "Locked" : "Scanning..."}
                    </span>
                </div>

                <div className="h-1 w-full bg-slate-800 rounded-full overflow-hidden">
                    {!foundSignal ? (
                        <motion.div
                            className="h-full bg-cyan-500 shadow-[0_0_10px_#06b6d4]"
                            animate={{ x: ["-100%", "100%"] }}
                            transition={{ duration: 1.5, repeat: Infinity, ease: "linear" }}
                        />
                    ) : (
                        <motion.div
                            className="h-full bg-emerald-500 shadow-[0_0_10px_#10b981]"
                            initial={{ width: 0 }}
                            animate={{ width: "100%" }}
                        />
                    )}
                </div>

                {foundSignal && (
                    <motion.div
                        initial={{ opacity: 0, y: 10 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="flex items-center justify-center gap-2 text-emerald-400 text-sm font-bold mt-2 bg-emerald-950/30 py-2 rounded-lg border border-emerald-900/50"
                    >
                        <Target size={16} />
                        <span>DEVICE FOUND</span>
                    </motion.div>
                )}
            </div>
        </div>
    );
}
