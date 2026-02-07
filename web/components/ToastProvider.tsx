'use client';

import React, { createContext, useContext, useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, CheckCircle, AlertCircle, Info } from 'lucide-react';

type ToastType = 'success' | 'error' | 'info';

interface Toast {
    id: string;
    message: string;
    type: ToastType;
}

interface ToastContextType {
    showToast: (message: string, type?: ToastType) => void;
}

const ToastContext = createContext<ToastContextType | undefined>(undefined);

export function useToast() {
    const context = useContext(ToastContext);
    if (!context) {
        throw new Error('useToast must be used within a ToastProvider');
    }
    return context;
}

export function ToastProvider({ children }: { children: React.ReactNode }) {
    const [toasts, setToasts] = useState<Toast[]>([]);

    const showToast = useCallback((message: string, type: ToastType = 'info') => {
        const id = Date.now().toString();
        setToasts(prev => [...prev, { id, message, type }]);

        // Auto remove after 3s
        setTimeout(() => {
            setToasts(prev => prev.filter(t => t.id !== id));
        }, 3000);
    }, []);

    const removeToast = (id: string) => {
        setToasts(prev => prev.filter(t => t.id !== id));
    };

    return (
        <ToastContext.Provider value={{ showToast }}>
            {children}
            <div className="fixed bottom-20 md:bottom-10 right-4 z-[100] flex flex-col gap-2 pointer-events-none">
                <AnimatePresence>
                    {toasts.map((toast) => (
                        <motion.div
                            key={toast.id}
                            initial={{ opacity: 0, x: 20, scale: 0.9 }}
                            animate={{ opacity: 1, x: 0, scale: 1 }}
                            exit={{ opacity: 0, x: 20, scale: 0.9 }}
                            layout
                            className={`pointer-events-auto min-w-[300px] max-w-[400px] p-4 rounded-xl border backdrop-blur-md shadow-2xl flex items-start gap-3
                                ${toast.type === 'success' ? 'bg-emerald-500/10 border-emerald-500/20 text-emerald-100' : ''}
                                ${toast.type === 'error' ? 'bg-rose-500/10 border-rose-500/20 text-rose-100' : ''}
                                ${toast.type === 'info' ? 'bg-indigo-500/10 border-indigo-500/20 text-indigo-100' : ''}
                            `}
                        >
                            <div className={`mt-1 shrink-0
                                ${toast.type === 'success' ? 'text-emerald-400' : ''}
                                ${toast.type === 'error' ? 'text-rose-400' : ''}
                                ${toast.type === 'info' ? 'text-indigo-400' : ''}
                            `}>
                                {toast.type === 'success' && <CheckCircle size={18} />}
                                {toast.type === 'error' && <AlertCircle size={18} />}
                                {toast.type === 'info' && <Info size={18} />}
                            </div>

                            <p className="text-sm font-medium leading-relaxed">{toast.message}</p>

                            <button
                                onClick={() => removeToast(toast.id)}
                                className="ml-auto text-white/50 hover:text-white transition-colors"
                            >
                                <X size={16} />
                            </button>
                        </motion.div>
                    ))}
                </AnimatePresence>
            </div>
        </ToastContext.Provider>
    );
}
