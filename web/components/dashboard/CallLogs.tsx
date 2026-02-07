'use client';

import { PhoneIncoming, PhoneOutgoing, PhoneMissed, Clock } from 'lucide-react';

export default function CallLogs({ logs = [] }: { logs?: any[] }) {
    // Helper to format duration (seconds) -> MM:SS
    const formatDuration = (seconds: number) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    // Helper for relative time
    const getRelativeTime = (timestamp: number) => {
        const diff = Date.now() - timestamp;
        const mins = Math.floor(diff / 60000);
        const hours = Math.floor(mins / 60);
        const days = Math.floor(hours / 24);

        if (mins < 1) return 'Just now';
        if (mins < 60) return `${mins} mins ago`;
        if (hours < 24) return `${hours} hours ago`;
        return `${days} days ago`;
    };

    return (
        <div className="h-full w-full p-6 transition-all duration-300">
            <h2 className="text-2xl font-bold text-white mb-6">Recent Call Logs</h2>

            <div className="space-y-2">
                {logs.length === 0 ? (
                    <div className="text-slate-500 text-center py-10 opacity-60">
                        <p>No call logs found on device.</p>
                        <p className="text-xs mt-2">Make sure Android Sync is enabled.</p>
                    </div>
                ) : logs.map((log, index) => {
                    // Android Call Types: 1 = Incoming, 2 = Outgoing, 3 = Missed
                    const isMissed = log.type === 3;
                    const isIncoming = log.type === 1;
                    const isOutgoing = log.type === 2;

                    return (
                        <div key={index} className="group p-4 rounded-xl hover:bg-white/5 border border-transparent hover:border-white/10 transition-all flex items-center justify-between cursor-default">
                            <div className="flex items-center gap-4">
                                <div className={`p-3 rounded-full 
                                    ${isMissed ? 'bg-rose-500/20 text-rose-400' :
                                        isIncoming ? 'bg-emerald-500/20 text-emerald-400' :
                                            'bg-slate-700/50 text-slate-400'}`}
                                >
                                    {isMissed ? <PhoneMissed size={20} /> :
                                        isIncoming ? <PhoneIncoming size={20} /> :
                                            <PhoneOutgoing size={20} />}
                                </div>
                                <div>
                                    <h3 className="font-bold text-slate-200 text-lg">{log.name || log.number || "Unknown"}</h3>
                                    <p className="text-slate-500 text-sm font-mono">{log.number}</p>
                                </div>
                            </div>

                            <div className="flex flex-col items-end gap-1">
                                <span className="text-white text-sm font-medium">{getRelativeTime(log.date)}</span>
                                <div className="flex items-center gap-2">
                                    <span className="text-xs text-slate-500 flex items-center gap-1">
                                        <Clock size={10} /> {formatDuration(log.duration)}
                                    </span>
                                    {/* Mock Status for Demo - In real app, this comes from separate AI log */}
                                    {isMissed && (
                                        <span className={`text-[10px] uppercase font-bold px-2 py-0.5 rounded-full bg-indigo-900/50 text-indigo-400 border border-indigo-800`}>
                                            WHISPERED
                                        </span>
                                    )}
                                </div>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
