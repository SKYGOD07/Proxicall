'use client';

import { motion } from 'framer-motion';
import { useEffect, useState } from 'react';

export default function LoadingScreen({ onComplete }: { onComplete: () => void }) {
    const [progress, setProgress] = useState(0);

    useEffect(() => {
        const interval = setInterval(() => {
            setProgress((prev) => {
                if (prev >= 100) {
                    clearInterval(interval);
                    setTimeout(onComplete, 500); // Small delay after 100%
                    return 100;
                }
                const increment = Math.random() * 15; // Random increment
                return Math.min(prev + increment, 100);
            });
        }, 150);

        return () => clearInterval(interval);
    }, [onComplete]);

    return (
        <motion.div
            className="fixed inset-0 z-50 bg-black flex flex-col items-center justify-center font-mono"
            initial={{ opacity: 1 }}
            exit={{ opacity: 0, transition: { duration: 0.8, ease: "easeInOut" } }}
        >
            {/* Nebula / Sci-Fi Core */}
            <div className="relative w-64 h-64 mb-12">
                {/* Rotating Rings */}
                <motion.div
                    className="absolute inset-0 border-4 border-cyan-500/20 rounded-full border-t-cyan-400"
                    animate={{ rotate: 360 }}
                    transition={{ duration: 2, repeat: Infinity, ease: "linear" }}
                />
                <motion.div
                    className="absolute inset-4 border-4 border-indigo-500/20 rounded-full border-b-indigo-400"
                    animate={{ rotate: -360 }}
                    transition={{ duration: 3, repeat: Infinity, ease: "linear" }}
                />
                <motion.div
                    className="absolute inset-8 border-2 border-slate-500/10 rounded-full"
                    animate={{ scale: [1, 1.1, 1] }}
                    transition={{ duration: 1.5, repeat: Infinity }}
                />

                {/* Core Glow */}
                <div className="absolute inset-0 m-auto w-32 h-32 bg-cyan-500/10 rounded-full blur-2xl animate-pulse" />
                <div className="absolute inset-0 m-auto w-16 h-16 bg-white/10 rounded-full blur-xl" />

                {/* Percentage Text */}
                <div className="absolute inset-0 flex items-center justify-center">
                    <span className="text-4xl font-bold bg-gradient-to-r from-cyan-400 to-indigo-500 bg-clip-text text-transparent">
                        {Math.round(progress)}%
                    </span>
                </div>
            </div>

            {/* Status Text with Typing Effect Placeholder */}
            <div className="space-y-2 text-center">
                <p className="text-cyan-400/80 text-sm tracking-widest uppercase">
                    Initializing ProxiCall Agent...
                </p>

                {/* Progress Bar */}
                <div className="w-64 h-1 bg-slate-800 rounded-full overflow-hidden mt-4">
                    <motion.div
                        className="h-full bg-gradient-to-r from-cyan-500 to-indigo-500"
                        initial={{ width: "0%" }}
                        animate={{ width: `${progress}%` }}
                    />
                </div>

                <div className="h-4">
                    <p className="text-xs text-slate-600">
                        {progress < 30 ? "Loading Core Modules..." :
                            progress < 60 ? "Connecting to Neural Network..." :
                                progress < 90 ? "Syncing Context Awareness..." :
                                    "System Ready."}
                    </p>
                </div>
            </div>
        </motion.div>
    );
}
