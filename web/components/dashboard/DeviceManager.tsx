'use client';

import { useState, useEffect } from 'react';
import { Shield, Smartphone, Wifi, MoreVertical, Battery, Lock, Trash2, Plus } from 'lucide-react';
import { useAuth } from '../AuthProvider';
import { db } from '@/lib/firebase';
import { collection, query, onSnapshot, addDoc, deleteDoc, doc, serverTimestamp } from 'firebase/firestore';

interface Device {
    id: string;
    name: string;
    type: 'Phone' | 'Wearable' | 'Laptop';
    status: 'Trusted' | 'Untrusted';
    battery: string;
    metered: boolean;
}

export default function DeviceManager() {
    const { user } = useAuth();
    const [devices, setDevices] = useState<Device[]>([]);
    const [isAdding, setIsAdding] = useState(false);

    useEffect(() => {
        if (!user) return;

        const q = query(collection(db, `users/${user.uid}/devices`));
        const unsubscribe = onSnapshot(q, (snapshot) => {
            const deviceList = snapshot.docs.map(doc => ({
                id: doc.id,
                ...doc.data()
            })) as Device[];
            setDevices(deviceList);
        });

        return () => unsubscribe();
    }, [user]);

    const addDevice = async () => {
        if (!user) return;
        setIsAdding(true);
        try {
            // Simulator: Adding a random device for demo purposes
            const types = ['Phone', 'Wearable', 'Laptop'];
            const names = ['Pixel 9 Pro', 'Galaxy Watch 7', 'MacBook Air'];
            const randomIdx = Math.floor(Math.random() * 3);

            await addDoc(collection(db, `users/${user.uid}/devices`), {
                name: names[randomIdx],
                type: types[randomIdx],
                status: 'Trusted',
                battery: `${Math.floor(Math.random() * 100)}%`,
                metered: Math.random() > 0.5,
                createdAt: serverTimestamp()
            });
        } catch (error) {
            console.error("Error adding device", error);
        } finally {
            setIsAdding(false);
        }
    };

    const removeDevice = async (id: string) => {
        if (!user) return;
        try {
            await deleteDoc(doc(db, `users/${user.uid}/devices`, id));
        } catch (error) {
            console.error("Error removing device", error);
        }
    };

    return (
        <div className="h-full w-full p-6">
            <div className="flex items-center justify-between mb-8">
                <div>
                    <h2 className="text-2xl font-bold text-white mb-2">Device Authentication</h2>
                    <p className="text-slate-400 text-sm">Manage trusted anchors for proximity fencing.</p>
                </div>
                <button
                    onClick={addDevice}
                    disabled={isAdding}
                    className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 rounded-lg text-sm font-bold shadow-lg shadow-indigo-500/20 transition-all flex items-center gap-2 disabled:opacity-50"
                >
                    <Plus size={16} />
                    {isAdding ? 'Adding...' : 'Add Device'}
                </button>
            </div>

            <div className="grid grid-cols-1 gap-4">
                {devices.length === 0 && (
                    <div className="text-center py-10 text-slate-500 border border-dashed border-white/10 rounded-2xl">
                        No devices found. Add a trusted device to secure your agent.
                    </div>
                )}

                {devices.map((device) => (
                    <div key={device.id} className="bg-white/5 border border-white/10 p-6 rounded-2xl flex items-center justify-between hover:bg-white/10 transition-colors group">
                        <div className="flex items-center gap-4">
                            <div className={`p-4 rounded-xl ${device.status === 'Trusted' ? 'bg-emerald-500/20 text-emerald-400' : 'bg-slate-700/50 text-slate-400'}`}>
                                {device.type === 'Phone' ? <Smartphone size={24} /> : device.type === 'Wearable' ? <Lock size={24} /> : <Wifi size={24} />}
                            </div>
                            <div>
                                <h3 className="text-lg font-bold text-slate-200 flex items-center gap-2">
                                    {device.name}
                                    {device.status === 'Trusted' && (
                                        <Shield size={14} className="text-emerald-400 fill-emerald-400/20" />
                                    )}
                                </h3>
                                <div className="flex items-center gap-4 text-xs text-slate-500 mt-1">
                                    <span className="flex items-center gap-1">
                                        <Battery size={12} /> {device.battery}
                                    </span>
                                    {device.metered && (
                                        <span className="px-2 py-0.5 bg-indigo-500/20 text-indigo-300 rounded text-[10px] font-bold uppercase">
                                            Metered Connection
                                        </span>
                                    )}
                                </div>
                            </div>
                        </div>

                        <div className="flex items-center gap-4">
                            <div className="text-right hidden sm:block">
                                <p className={`text-sm font-bold ${device.status === 'Trusted' ? 'text-emerald-400' : 'text-slate-500'}`}>
                                    {device.status}
                                </p>
                                <p className="text-xs text-slate-600">Synced via Cloud</p>
                            </div>
                            <button
                                onClick={() => removeDevice(device.id)}
                                className="p-2 hover:bg-rose-500/20 rounded-full text-slate-400 hover:text-rose-400 transition-colors"
                            >
                                <Trash2 size={20} />
                            </button>
                        </div>
                    </div>
                ))}
            </div>

            <div className="mt-8 p-4 bg-amber-500/10 border border-amber-500/30 rounded-xl flex gap-4">
                <Lock className="text-amber-400 shrink-0" size={24} />
                <div>
                    <h4 className="text-amber-400 font-bold text-sm">Security Note</h4>
                    <p className="text-amber-200/70 text-xs mt-1">
                        Metered authentication ensures your agent only acts when connected to a validated, high-bandwidth trusted device to prevent data leaks.
                    </p>
                </div>
            </div>
        </div>
    );
}
