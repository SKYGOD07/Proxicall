'use client';

import { Star, User, Bell, Clock, Briefcase, Heart } from 'lucide-react';

import { useState } from 'react';
import { useToast } from '../ToastProvider';

export default function ContactManager({ contacts = [] }: { contacts?: any[] }) {
    const { showToast } = useToast();
    const [requestingPermission, setRequestingPermission] = useState(false);
    const [permissionGranted, setPermissionGranted] = useState(false);

    const handleAddRuleClick = () => {
        if (contacts.length === 0) {
            showToast("No contacts synced. Please sync via Android App.", "error");
        } else {
            showToast("Context Rule Editor is coming soon!", "info");
        }
    };

    return (
        <div className="h-full w-full p-6">
            <h2 className="text-2xl font-bold text-white mb-6">Contact Rules Engine</h2>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {/* Add New Card / Permission Request */}
                <button
                    onClick={handleAddRuleClick}
                    disabled={requestingPermission}
                    className="group h-full min-h-[220px] relative overflow-hidden rounded-2xl border-2 border-dashed border-slate-700 hover:border-cyan-500 hover:bg-cyan-950/20 transition-all flex flex-col items-center justify-center gap-4 text-center p-6"
                >
                    {requestingPermission ? (
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-cyan-400 mb-2"></div>
                    ) : (
                        <>
                            <div className="w-14 h-14 rounded-full bg-slate-800 group-hover:bg-cyan-500/20 flex items-center justify-center transition-colors border border-slate-600 group-hover:border-cyan-400/50">
                                <span className="text-3xl text-slate-500 group-hover:text-cyan-400 font-light transition-colors">+</span>
                            </div>
                            <div>
                                <h3 className="text-slate-300 font-bold mb-1 transition-colors group-hover:text-cyan-300">
                                    {permissionGranted ? "Add Custom Rule" : "Connect Contacts"}
                                </h3>
                                <p className="text-xs text-slate-500 max-w-[200px] leading-relaxed group-hover:text-slate-400">
                                    {permissionGranted
                                        ? "Create a new behavior rule for a specific contact."
                                        : "Allow access to Google Contacts to enable context-aware routing."}
                                </p>
                            </div>
                        </>
                    )}
                </button>

                {contacts.length === 0 ? (
                    <div className="col-span-1 md:col-span-2 flex flex-col items-center justify-center p-8 bg-slate-900/50 rounded-2xl border border-white/5">
                        <p className="text-slate-500 mb-2">No Android Contacts Synced</p>
                        <p className="text-xs text-slate-600">Waiting for data from ProxiCall App...</p>
                    </div>
                ) : contacts.map((contact, index) => (
                    <div key={index} className="bg-slate-900 rounded-2xl p-6 border border-slate-800 hover:border-indigo-500/50 transition-all hover:shadow-lg hover:shadow-indigo-500/10 relative group">
                        <div className="flex justify-between items-start mb-4">
                            <div className="w-12 h-12 rounded-full bg-slate-800 flex items-center justify-center border border-slate-700">
                                <User className="text-slate-400" size={20} />
                            </div>
                            <span className={`px-2 py-1 rounded text-[10px] uppercase font-bold tracking-wider bg-slate-800 text-slate-400`}>
                                Standard
                            </span>
                        </div>

                        <h3 className="text-xl font-bold text-white mb-1">{contact.name}</h3>
                        <p className="text-slate-500 text-xs font-mono mb-4">{contact.phoneNumber}</p>

                        <div className="space-y-2">
                            <div className="p-3 rounded-lg bg-black/40 border border-white/5 opacity-50">
                                <p className="text-slate-500 text-xs italic">No custom rules active.</p>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
