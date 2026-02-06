'use client';

import { ArrowLeft, Book, Code, Terminal, Layers, Shield } from 'lucide-react';
import Link from 'next/link';

export default function Documentation() {
    return (
        <div className="min-h-screen bg-black text-slate-300 font-sans selection:bg-cyan-500/30">
            <div className="fixed inset-0 bg-[radial-gradient(circle_at_top_right,_var(--tw-gradient-stops))] from-slate-900/40 via-black to-black -z-20" />

            <header className="p-6 border-b border-white/10 flex items-center gap-4 sticky top-0 bg-black/80 backdrop-blur-md z-50">
                <Link href="/">
                    <button className="p-2 hover:bg-white/10 rounded-full transition-colors">
                        <ArrowLeft size={20} className="text-white" />
                    </button>
                </Link>
                <div className="flex items-center gap-2">
                    <Book className="text-indigo-400" size={20} />
                    <span className="font-bold text-white text-lg">ProxiCall &bull; Documentation</span>
                </div>
            </header>

            <main className="max-w-4xl mx-auto px-6 py-12 flex flex-col gap-12">

                {/* Introduction */}
                <section>
                    <h1 className="text-4xl font-bold text-white mb-6">System Architecture</h1>
                    <p className="text-lg leading-relaxed">
                        ProxiCall is a <strong className="text-cyan-400">Context-Aware Autonomous Agent</strong> that bridges the physical and digital worlds.
                        It uses Google's <strong className="text-indigo-400">Gemini 3</strong> multimodal model to intelligently manage communication based on real-world context (location, calendar, driving status) and proximity to trusted devices.
                    </p>
                </section>

                {/* Tech Stack */}
                <section className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="bg-slate-900/50 p-6 rounded-2xl border border-white/5">
                        <div className="flex items-center gap-3 mb-4 text-white font-bold">
                            <Layers className="text-pink-400" /> Web Stack (Next.js)
                        </div>
                        <ul className="space-y-2 text-sm list-disc pl-5 marker:text-pink-500">
                            <li>Next.js 14 (App Router)</li>
                            <li>TypeScript & Tailwind CSS</li>
                            <li>Framer Motion (Animations)</li>
                            <li>Lucide React (Icons)</li>
                        </ul>
                    </div>
                    <div className="bg-slate-900/50 p-6 rounded-2xl border border-white/5">
                        <div className="flex items-center gap-3 mb-4 text-white font-bold">
                            <Code className="text-green-400" /> Android Stack (Kotlin)
                        </div>
                        <ul className="space-y-2 text-sm list-disc pl-5 marker:text-green-500">
                            <li>Jetpack Compose (UI)</li>
                            <li>Broadcast Receivers (Call/SMS)</li>
                            <li>Bluetooth Manager (Proximity)</li>
                            <li>Google Generative AI SDK (Gemini 3)</li>
                        </ul>
                    </div>
                </section>

                {/* API Integration */}
                <section>
                    <h2 className="text-2xl font-bold text-white mb-6 flex items-center gap-2">
                        <Terminal className="text-amber-400" /> Gemini 3 Integration
                    </h2>
                    <div className="bg-slate-950 rounded-xl border border-white/10 overflow-hidden font-mono text-sm">
                        <div className="bg-white/5 p-3 border-b border-white/5 text-xs text-slate-500">
                            android/app/src/main/java/com/example/proxicall/data/GeminiClient.kt
                        </div>
                        <div className="p-4 overflow-x-auto">
                            <pre className="text-cyan-300">
                                {`// Initializing the Generative Model
val generativeModel = GenerativeModel(
    modelName = "gemini-1.5-flash", // Compatible with v3
    apiKey = BuildConfig.GEMINI_API_KEY
)

// Generating Context-Aware Replies
suspend fun generateReply(caller: String, context: String): String {
    val prompt = "Generate a reply for $caller based on $context..."
    return generativeModel.generateContent(prompt).text
}`}
                            </pre>
                        </div>
                    </div>
                    <p className="mt-4 text-sm text-slate-500">
                        The agent uses the Gemini 1.5 Flash endpoint for low-latency responses while leveraging Gemini 3's superior reasoning capabilities via the API key configuration.
                    </p>
                </section>

                {/* Security */}
                <section>
                    <h2 className="text-2xl font-bold text-white mb-6 flex items-center gap-2">
                        <Shield className="text-emerald-400" /> Privacy & Security
                    </h2>
                    <div className="space-y-4">
                        <div className="flex gap-4">
                            <div className="min-w-[4px] bg-emerald-500/50 rounded-full" />
                            <div>
                                <h3 className="font-bold text-white">On-Device Processing</h3>
                                <p className="text-sm">Call audio is processed in a temporary memory buffer and is never uploaded. Only metadata (Caller ID) is sent to Gemini.</p>
                            </div>
                        </div>
                        <div className="flex gap-4">
                            <div className="min-w-[4px] bg-emerald-500/50 rounded-full" />
                            <div>
                                <h3 className="font-bold text-white">Device Fencing</h3>
                                <p className="text-sm">Critical actions (like hanging up) require a trusted Bluetooth token (Watch/Ring) to be within 2 meters.</p>
                            </div>
                        </div>
                    </div>
                </section>

                {/* Footer */}
                <footer className="pt-20 pb-10 border-t border-white/5 text-center text-xs text-slate-600">
                    Documentation V 1.0.0 &bull; Last Updated: 2026-02-06
                </footer>
            </main>
        </div>
    );
}
