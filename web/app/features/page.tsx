'use client';

import { motion } from 'framer-motion';
import { ArrowLeft, Shield, Cpu, Waves, Lock, Battery, Globe } from 'lucide-react';
import Link from 'next/link';

export default function Features() {
    const features = [
        {
            title: "Gemini 3 Core",
            description: "Powered by Google's latest Gemini 3 model for unprecedented understanding of conversation context.",
            icon: Cpu,
            gradient: "from-cyan-400 to-blue-500"
        },
        {
            title: "Whisper Mode via BLE",
            description: "The Agent intercepts audio and 'whispers' a summary of the caller's intent to your earbud without ringing the phone.",
            icon: Waves,
            gradient: "from-indigo-400 to-purple-500"
        },
        {
            title: "Device Fencing",
            description: "Only authorizes actions when specific trusted hardware tokens (watch, ring) are within 2 meters.",
            icon: Shield,
            gradient: "from-emerald-400 to-teal-500"
        },
        {
            title: "On-Device Privacy",
            description: " sensitive call data is processed ethically using Google's Safety Principles. No audio is stored permanently.",
            icon: Lock,
            gradient: "from-rose-400 to-orange-500"
        },
        {
            title: "Battery Optimized",
            description: "Uses Low Energy Bluetooth (BLE) scanning to run 24/7 with less than 2% battery impact.",
            icon: Battery,
            gradient: "from-amber-400 to-yellow-500"
        },
        {
            title: "Global Context",
            description: "Checks your Calendar, Location, and Driving Status to make the smartest decision possible.",
            icon: Globe,
            gradient: "from-blue-400 to-cyan-500"
        }
    ];

    return (
        <div className="min-h-screen bg-black text-white font-sans">
            <div className="fixed inset-0 bg-[radial-gradient(ellipse_at_center,_var(--tw-gradient-stops))] from-indigo-950/20 via-black to-black -z-20" />

            <header className="p-6 flex items-center justify-between border-b border-white/10 backdrop-blur-md sticky top-0 z-50">
                <Link href="/">
                    <button className="flex items-center gap-2 text-slate-400 hover:text-white transition-colors">
                        <ArrowLeft size={20} />
                        <span className="font-bold tracking-wide">Back to Home</span>
                    </button>
                </Link>
                <div className="text-xl font-bold bg-gradient-to-r from-purple-400 to-pink-500 bg-clip-text text-transparent">
                    System Capabilities
                </div>
            </header>

            <main className="max-w-7xl mx-auto px-6 py-20">
                <div className="text-center mb-20">
                    <h1 className="text-5xl md:text-7xl font-bold bg-clip-text text-transparent bg-gradient-to-b from-white to-slate-500 mb-6">
                        Beyond Smart. <br />
                        <span className="text-indigo-500">Truly Intelligent.</span>
                    </h1>
                    <p className="text-slate-400 text-xl max-w-2xl mx-auto">
                        ProxiCall isn't just an answering machine. It's a digital bodyguard for your attention span.
                    </p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                    {features.map((feature, index) => (
                        <motion.div
                            key={index}
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ delay: index * 0.1 }}
                            className="bg-slate-900/50 border border-white/5 p-8 rounded-3xl hover:bg-slate-800/50 transition-all hover:-translate-y-2 hover:shadow-2xl group"
                        >
                            <div className={`w-14 h-14 rounded-2xl bg-gradient-to-br ${feature.gradient} flex items-center justify-center mb-6 shadow-lg group-hover:scale-110 transition-transform`}>
                                <feature.icon size={28} className="text-white" />
                            </div>
                            <h3 className="text-2xl font-bold text-white mb-2">{feature.title}</h3>
                            <p className="text-slate-400 leading-relaxed font-medium">
                                {feature.description}
                            </p>
                        </motion.div>
                    ))}
                </div>
            </main>
        </div>
    );
}
