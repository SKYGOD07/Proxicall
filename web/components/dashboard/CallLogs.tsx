'use client';

import { PhoneIncoming, PhoneOutgoing, PhoneMissed, Clock } from 'lucide-react';

export default function CallLogs() {
    const logs = [
        { id: 1, name: "Mom", number: "(555) 123-4567", type: "missed", time: "2 mins ago", duration: "0:00", status: "Auto-Replied" },
        { id: 2, name: "Boss (Work)", number: "(555) 987-6543", type: "missed", time: "15 mins ago", duration: "0:00", status: "Whispered" },
        { id: 3, name: "Unknown Caller", number: "+1 (415) 555-0000", type: "incoming", time: "1 hour ago", duration: "2:14", status: "Answered" },
        { id: 4, name: "Mike S.", number: "(555) 555-5555", type: "outgoing", time: "3 hours ago", duration: "5:30", status: "Completed" },
    ];

    return (
        <div className="h-full w-full p-6">
            <h2 className="text-2xl font-bold text-white mb-6">Recent Call Logs</h2>

            <div className="space-y-2">
                {logs.map((log) => (
                    <div key={log.id} className="group p-4 rounded-xl hover:bg-white/5 border border-transparent hover:border-white/10 transition-all flex items-center justify-between cursor-default">
                        <div className="flex items-center gap-4">
                            <div className={`p-3 rounded-full 
                                ${log.type === 'missed' ? 'bg-rose-500/20 text-rose-400' :
                                    log.type === 'incoming' ? 'bg-emerald-500/20 text-emerald-400' :
                                        'bg-slate-700/50 text-slate-400'}`}
                            >
                                {log.type === 'missed' ? <PhoneMissed size={20} /> :
                                    log.type === 'incoming' ? <PhoneIncoming size={20} /> :
                                        <PhoneOutgoing size={20} />}
                            </div>
                            <div>
                                <h3 className="font-bold text-slate-200 text-lg">{log.name}</h3>
                                <p className="text-slate-500 text-sm font-mono">{log.number}</p>
                            </div>
                        </div>

                        <div className="flex flex-col items-end gap-1">
                            <span className="text-white text-sm font-medium">{log.time}</span>
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-slate-500 flex items-center gap-1">
                                    <Clock size={10} /> {log.duration}
                                </span>
                                {log.status && (
                                    <span className={`text-[10px] uppercase font-bold px-2 py-0.5 rounded-full
                                        ${log.status === 'Auto-Replied' ? 'bg-cyan-900/50 text-cyan-400 border border-cyan-800' :
                                            log.status === 'Whispered' ? 'bg-indigo-900/50 text-indigo-400 border border-indigo-800' :
                                                'bg-slate-800 text-slate-500'}`}
                                    >
                                        {log.status}
                                    </span>
                                )}
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
