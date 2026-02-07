'use client';

import { MessageSquare, Phone, Activity, Clock } from 'lucide-react';

interface ActivityLog {
    action: string;
    caller: string;
    timestamp: number;
    response: string;
}

export default function ActivityLogViewer({ logs = [] }: { logs?: ActivityLog[] }) {
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
            <h2 className="text-2xl font-bold text-white mb-6">AI Activity Logs</h2>

            <div className="space-y-2">
                {logs.length === 0 ? (
                    <div className="text-slate-500 text-center py-10 opacity-60">
                        <p>No activity logs found.</p>
                        <p className="text-xs mt-2">AI actions will appear here.</p>
                    </div>
                ) : logs.map((log, index) => {
                    const isSms = log.action === 'SMS_SENT';
                    const isCall = log.action === 'CALL_ANSWERED';

                    return (
                        <div key={index} className="group p-4 rounded-xl hover:bg-white/5 border border-transparent hover:border-white/10 transition-all flex flex-col gap-3 cursor-default">
                            <div className="flex items-start justify-between">
                                <div className="flex items-center gap-4">
                                    <div className={`p-3 rounded-full 
                                        ${isSms ? 'bg-indigo-500/20 text-indigo-400' :
                                            isCall ? 'bg-emerald-500/20 text-emerald-400' :
                                                'bg-slate-700/50 text-slate-400'}`}
                                    >
                                        {isSms ? <MessageSquare size={20} /> :
                                            isCall ? <Phone size={20} /> :
                                                <Activity size={20} />}
                                    </div>
                                    <div>
                                        <h3 className="font-bold text-slate-200 text-lg">{log.action.replace('_', ' ')}</h3>
                                        <p className="text-slate-500 text-sm font-mono">From: {log.caller}</p>
                                    </div>
                                </div>

                                <div className="flex flex-col items-end gap-1">
                                    <span className="text-white text-sm font-medium">{getRelativeTime(log.timestamp)}</span>
                                    <span className="text-xs text-slate-500 flex items-center gap-1">
                                        <Clock size={10} /> {new Date(log.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                    </span>
                                </div>
                            </div>

                            {log.response && (
                                <div className="ml-14 p-3 bg-white/5 rounded-lg border border-white/5 text-sm text-slate-300 italic">
                                    "{log.response}"
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
