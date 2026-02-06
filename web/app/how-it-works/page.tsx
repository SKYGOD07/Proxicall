'use client';

import { motion } from 'framer-motion';
import { ArrowLeft, Smartphone, Radio, Mic, MessageSquare, Zap } from 'lucide-react';
import Link from 'next/link';

export default function HowItWorks() {
    const steps = [
        {
            id: 1,
            title: "Proximity Detection",
            description: "The Agent constantly scans for your specific Bluetooth beacon (Smartwatch or Phone).",
            icon: Radio,
            color: "text-cyan-400",
            bg: "bg-cyan-950/30"
        },
        {
            id: 2,
            title: "Context Analysis",
            description: "When a call comes in, ProxiCall checks your status: Are you driving? In a meeting? Sleeping?",
            icon: Smartphone,
            color: "text-indigo-400",
            bg: "bg-indigo-950/30"
        },
        {
            id: 3,
            title: "Gemini Decision",
            description: "Google's Gemini 3 analyzes the caller ID and your context to decide the best action.",
            icon: Zap,
            color: "text-amber-400",
            bg: "bg-amber-950/30"
        },
        {
            id: 4,
            title: "Autonomous Action",
            description: "The Agent either auto-replies via SMS, whispers a summary to your ear, or lets the call ring.",
            icon: MessageSquare,
            color: "text-emerald-400",
            bg: "bg-emerald-950/30"
        }
    ];

    return (
        <div className="min-h-screen bg-black text-white font-sans selection:bg-cyan-500/30">
            <div className="fixed inset-0 bg-[radial-gradient(circle_at_top,_var(--tw-gradient-stops))] from-slate-900 via-black to-black -z-20" />

            <header className="p-6 flex items-center justify-between border-b border-white/10 backdrop-blur-md sticky top-0 z-50">
                <Link href="/">
                    <button className="flex items-center gap-2 text-slate-400 hover:text-white transition-colors">
                        <ArrowLeft size={20} />
                        <span className="font-bold tracking-wide">Back to Home</span>
                    </button>
                </Link>
                <div className="text-xl font-bold bg-gradient-to-r from-cyan-400 to-indigo-500 bg-clip-text text-transparent">
                    How ProxiCall Works
                </div>
            </header>

            <main className="max-w-4xl mx-auto px-6 py-20">
                <div className="relative border-l-2 border-slate-800 ml-4 md:ml-10 space-y-20">
                    {steps.map((step, index) => (
                        <motion.div
                            key={step.id}
                            initial={{ opacity: 0, x: -50 }}
                            whileInView={{ opacity: 1, x: 0 }}
                            viewport={{ once: true }}
                            transition={{ delay: index * 0.2 }}
                            className="relative pl-12 md:pl-20"
                        >
                            {/* Timeline Node */}
                            <div className={`absolute -left-[9px] top-0 w-5 h-5 rounded-full border-4 border-black ${step.color.replace('text', 'bg')} shadow-[0_0_20px_currentColor]`} />

                            <div className="bg-white/5 border border-white/10 p-8 rounded-3xl hover:bg-white/10 transition-colors group">
                                <div className={`w-16 h-16 rounded-2xl ${step.bg} flex items-center justify-center mb-6`}>
                                    <step.icon size={32} className={step.color} />
                                </div>
                                <h3 className="text-3xl font-bold text-white mb-4 flex items-center gap-4">
                                    <span className="opacity-20 text-4xl">0{step.id}</span>
                                    {step.title}
                                </h3>
                                <p className="text-slate-400 text-lg leading-relaxed">
                                    {step.description}
                                </p>
                            </div>
                        </motion.div>
                    ))}
                </div>

                <div className="mt-20 text-center">
                    <Link href="/dashboard">
                        <button className="px-12 py-4 bg-white text-black font-bold rounded-full hover:scale-105 hover:shadow-[0_0_40px_rgba(255,255,255,0.3)] transition-all">
                            Start the Agent
                        </button>
                    </Link>
                </div>
            </main>
        </div>
    );
}
