'use client';

import { Check, X, AlertTriangle, MessageSquare } from 'lucide-react';

export default function AIVerification() {
    const aiActions: any[] = [
        // No actions verified yet
    ];

    return (
        <div className="h-full w-full p-6">
            <div className="mb-6">
                <h2 className="text-2xl font-bold text-white">AI Action Verification</h2>
                <p className="text-slate-400 text-sm">Review and audit autonomous decisions made by Gemini.</p>
            </div>

            <div className="space-y-4">
                {aiActions.length === 0 ? (
                    <div className="flex flex-col items-center justify-center p-10 text-slate-500 border border-dashed border-white/10 rounded-xl">
                        <MessageSquare size={32} className="mb-2 opacity-50" />
                        <p>No recent AI actions to verify.</p>
                    </div>
                ) : (
                    aiActions.map((action) => (
                        <div key={action.id} className="bg-slate-900/50 border border-slate-800 rounded-xl p-5 relative overflow-hidden">
                            {/* Status Bar */}
                            <div className={`absolute left-0 top-0 bottom-0 w-1 
                            ${action.flagged ? 'bg-amber-500' : 'bg-emerald-500'}`}
                            />

                            <div className="flex justify-between items-start mb-4">
                                <div>
                                    <h3 className="text-slate-200 font-bold flex items-center gap-2">
                                        To: <span className="text-white">{action.to}</span>
                                        <span className="text-xs font-normal text-slate-500 px-2 py-0.5 bg-slate-800 rounded-full">{action.trigger}</span>
                                    </h3>
                                    <p className="text-xs text-slate-500 mt-1">Context Provided: "{action.context}"</p>
                                </div>
                                <div className="flex gap-2">
                                    <button className="p-2 bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 rounded-lg transition-colors" title="Verify Accurate">
                                        <Check size={18} />
                                    </button>
                                    <button className="p-2 bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 rounded-lg transition-colors" title="Flag Error">
                                        <X size={18} />
                                    </button>
                                </div>
                            </div>

                            {/* Message Preview */}
                            <div className="bg-black/40 rounded-lg p-3 border border-white/5 flex gap-3">
                                <MessageSquare size={16} className="text-indigo-400 shrink-0 mt-1" />
                                <div>
                                    <p className="text-sm text-indigo-100 font-medium leading-relaxed">
                                        "{action.response}"
                                    </p>
                                </div>
                            </div>

                            {action.flagged && (
                                <div className="flex items-center gap-2 mt-3 text-amber-400 text-xs font-bold">
                                    <AlertTriangle size={12} />
                                    Flagged for Review: Potential hallucination or context mismatch.
                                </div>
                            )}
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}
