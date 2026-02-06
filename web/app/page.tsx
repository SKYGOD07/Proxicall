'use client';

import { motion, useScroll, useTransform, useSpring } from 'framer-motion';
import { useRef, useState } from 'react';
import Link from 'next/link';
import { ChevronDown, ArrowRight, Activity, Smartphone, Server, BrainCircuit } from 'lucide-react';
import RadarAnimation from '../components/RadarAnimation';

export default function LandingPage() {
    const { scrollYProgress } = useScroll();
    const scaleX = useSpring(scrollYProgress, {
        stiffness: 100,
        damping: 30,
        restDelta: 0.001
    });

    return (
        <div className="min-h-screen bg-black text-white overflow-x-hidden font-sans selection:bg-cyan-500/30">
            {/* Progress Bar */}
            <motion.div
                className="fixed top-0 left-0 right-0 h-1 bg-gradient-to-r from-cyan-500 to-indigo-500 origin-left z-50"
                style={{ scaleX }}
            />

            {/* Glassmorphism Navbar */}
            <nav className="fixed top-0 inset-x-0 z-40 p-6 flex justify-between items-center bg-black/5 backdrop-blur-md border-b border-white/5">
                <div className="flex items-center gap-2">
                    <Activity className="text-cyan-400" />
                    <span className="font-bold tracking-wider">PROXICALL</span>
                </div>
                <div className="flex gap-4 text-sm font-medium text-slate-300">
                    <span className="cursor-pointer hover:text-white transition-colors">How it Works</span>
                    <span className="cursor-pointer hover:text-white transition-colors">Features</span>
                </div>
                <Link href="/dashboard">
                    <button className="bg-white/10 hover:bg-white/20 border border-white/10 backdrop-blur-md px-6 py-2 rounded-full text-sm font-semibold transition-all hover:scale-105 active:scale-95 flex items-center gap-2">
                        Launch Agent <ArrowRight size={14} />
                    </button>
                </Link>
            </nav>

            {/* Hero Section */}
            <section className="h-screen flex flex-col items-center justify-center relative px-6">
                <div className="absolute inset-0 bg-[radial-gradient(circle_at_center,_var(--tw-gradient-stops))] from-indigo-900/20 via-black to-black opacity-50" />

                <motion.div
                    initial={{ opacity: 0, y: 30 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.8 }}
                    className="z-10 text-center max-w-4xl"
                >
                    <div className="inline-block mb-4 px-4 py-1.5 rounded-full border border-cyan-500/30 bg-cyan-900/10 backdrop-blur-md text-cyan-400 text-xs tracking-widest uppercase">
                        System Online • V 1.0.0
                    </div>
                    <h1 className="text-5xl md:text-7xl font-bold mb-6 leading-tight bg-gradient-to-br from-white via-slate-200 to-slate-500 bg-clip-text text-transparent">
                        The Context-Aware <br />
                        <span className="text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-indigo-500">Autonomous Agent</span>
                    </h1>
                    <p className="text-lg md:text-xl text-slate-400 mb-10 max-w-2xl mx-auto leading-relaxed">
                        ProxiCall fuses Google Gemini's reasoning with real-time phone status to handle your communication autonomously.
                    </p>

                    <div className="flex flex-col md:flex-row gap-4 justify-center items-center">
                        <Link href="/dashboard">
                            <motion.button
                                whileHover={{ scale: 1.05 }}
                                whileTap={{ scale: 0.95 }}
                                className="px-8 py-4 bg-gradient-to-r from-cyan-600 to-indigo-600 rounded-full font-bold text-lg shadow-[0_0_20px_rgba(34,211,238,0.3)] hover:shadow-[0_0_30px_rgba(34,211,238,0.5)] transition-shadow"
                            >
                                Initialize Agent
                            </motion.button>
                        </Link>
                        <button className="px-8 py-4 bg-slate-900 rounded-full font-semibold border border-slate-800 hover:bg-slate-800 transition-colors">
                            View Documentation
                        </button>
                    </div>
                </motion.div>

                {/* Radar Animation Background/Overlay */}
                <div className="absolute inset-0 z-0 opacity-20 pointer-events-none scale-150">
                    <div className="absolute inset-0 flex items-center justify-center">
                        <RadarAnimation />
                    </div>
                </div>

                <motion.div
                    animate={{ y: [0, 10, 0] }}
                    transition={{ duration: 2, repeat: Infinity }}
                    className="absolute bottom-12 text-slate-500 flex flex-col items-center gap-2 text-xs uppercase tracking-widest"
                >
                    Scroll needed
                    <ChevronDown className="text-cyan-500" />
                </motion.div>
            </section>

            {/* How it Works - Scroll Animation Section */}
            <HowItWorks />

            {/* Footer */}
            <footer className="py-20 border-t border-slate-900 bg-black text-center relative overflow-hidden">
                <div className="absolute inset-0 bg-gradient-to-t from-indigo-900/10 to-transparent" />
                <div className="relative z-10">
                    <h2 className="text-3xl font-bold mb-8">Ready to deploy?</h2>
                    <Link href="/dashboard">
                        <button className="px-10 py-5 bg-white text-black rounded-full font-bold text-xl hover:bg-cyan-50 transition-colors shadow-2xl">
                            Launch Dashboard
                        </button>
                    </Link>
                    <p className="mt-8 text-slate-600 text-sm">© 2026 ProxiCall • Powered by Google Gemini</p>
                </div>
            </footer>
        </div>
    );
}

function HowItWorks() {
    return (
        <section className="py-32 px-6 max-w-6xl mx-auto">
            <div className="mb-24 text-center">
                <h2 className="text-3xl md:text-5xl font-bold mb-4">Autonomous Workflow</h2>
                <p className="text-slate-400">From signal detection to intelligent response.</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-8 relative">
                {/* Connecting Line */}
                <div className="hidden md:block absolute top-12 left-0 right-0 h-0.5 bg-gradient-to-r from-cyan-900 via-indigo-900 to-purple-900 -z-10" />

                <StepCard
                    icon={<Smartphone size={32} className="text-cyan-400" />}
                    title="1. Signal Detection"
                    desc="ProxiCall monitors Bluetooth proximity and phone state in real-time using Android APIs."
                    delay={0.2}
                />
                <StepCard
                    icon={<BrainCircuit size={32} className="text-indigo-400" />}
                    title="2. Context Analysis"
                    desc="Gemini 1.5 Flash analyzes your calendar, location, and recent notifications to understand availability."
                    delay={0.4}
                />
                <StepCard
                    icon={<Server size={32} className="text-purple-400" />}
                    title="3. Smart Action"
                    desc="The agent decides to auto-reply via SMS or engage 'Whisper Mode' for urgent voice interactions."
                    delay={0.6}
                />
            </div>
        </section>
    );
}

function StepCard({ icon, title, desc, delay }: { icon: React.ReactNode, title: string, desc: string, delay: number }) {
    return (
        <motion.div
            initial={{ opacity: 0, y: 50 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6, delay }}
            className="bg-slate-900/50 border border-white/5 p-8 rounded-3xl backdrop-blur-sm hover:border-cyan-500/30 transition-colors group"
        >
            <div className="mb-6 p-4 bg-black/50 rounded-2xl w-fit border border-white/5 group-hover:scale-110 transition-transform duration-500">
                {icon}
            </div>
            <h3 className="text-xl font-bold mb-4">{title}</h3>
            <p className="text-slate-400 leading-relaxed text-sm">{desc}</p>
        </motion.div>
    );
}
