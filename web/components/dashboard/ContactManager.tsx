'use client';

import { Star, User, Bell, Clock, Briefcase, Heart } from 'lucide-react';

export default function ContactManager() {
    const contacts = [
        { id: 1, name: "Mom", relation: "Family", urgency: "High", customRule: "Always share location", icon: Heart, color: "text-rose-400" },
        { id: 2, name: "Boss", relation: "Professional", urgency: "Medium", customRule: "Formal replies only", icon: Briefcase, color: "text-amber-400" },
        { id: 3, name: "Sarah (Gym)", relation: "Friend", urgency: "Low", customRule: "Ignore during work hours", icon: User, color: "text-cyan-400" },
    ];

    return (
        <div className="h-full w-full p-6">
            <h2 className="text-2xl font-bold text-white mb-6">Contact Rules Engine</h2>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {/* Add New Card */}
                <button className="h-full min-h-[200px] border-2 border-dashed border-slate-700 hover:border-cyan-500 hover:bg-cyan-950/20 rounded-2xl flex flex-col items-center justify-center gap-3 transition-all group">
                    <div className="w-12 h-12 rounded-full bg-slate-800 group-hover:bg-cyan-500/20 flex items-center justify-center transition-colors">
                        <span className="text-2xl text-slate-500 group-hover:text-cyan-400 font-light">+</span>
                    </div>
                    <span className="text-slate-500 group-hover:text-cyan-300 font-medium">Add Context Rule</span>
                </button>

                {contacts.map((contact) => (
                    <div key={contact.id} className="bg-slate-900 rounded-2xl p-6 border border-slate-800 hover:border-indigo-500/50 transition-all hover:shadow-lg hover:shadow-indigo-500/10 relative group">
                        <div className="flex justify-between items-start mb-4">
                            <div className="w-12 h-12 rounded-full bg-slate-800 flex items-center justify-center border border-slate-700">
                                <contact.icon className={contact.color} size={20} />
                            </div>
                            <span className={`px-2 py-1 rounded text-[10px] uppercase font-bold tracking-wider
                                ${contact.urgency === 'High' ? 'bg-rose-950 text-rose-400' :
                                    contact.urgency === 'Medium' ? 'bg-amber-950 text-amber-400' :
                                        'bg-slate-800 text-slate-400'}`}
                            >
                                {contact.urgency} Priority
                            </span>
                        </div>

                        <h3 className="text-xl font-bold text-white mb-1">{contact.name}</h3>
                        <p className="text-slate-500 text-xs uppercase font-bold tracking-widest mb-4">{contact.relation}</p>

                        <div className="space-y-2">
                            <div className="p-3 rounded-lg bg-black/40 border border-white/5">
                                <p className="text-slate-400 text-xs mb-1">Custom Implementation:</p>
                                <p className="text-indigo-300 text-sm font-medium">"{contact.customRule}"</p>
                            </div>
                        </div>

                        <div className="absolute top-4 right-4 opacity-0 group-hover:opacity-100 transition-opacity">
                            <button className="text-slate-400 hover:text-white">
                                <Star size={18} />
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
