'use client';

import { motion } from 'framer-motion';
import { Bluetooth, Smartphone, ShieldCheck, Check } from 'lucide-react';

interface ConnectionBreadcrumbProps {
    status: 'scanning' | 'connected' | 'authenticated';
}

export default function ConnectionBreadcrumb({ status }: ConnectionBreadcrumbProps) {
    const steps = [
        { id: 'scanning', label: 'Bluetooth Active', icon: Bluetooth },
        { id: 'connected', label: 'Device Connected', icon: Smartphone },
        { id: 'authenticated', label: 'Call Auth Ready', icon: ShieldCheck },
    ];

    const getCurrentStepIndex = () => {
        switch (status) {
            case 'scanning': return 0;
            case 'connected': return 1;
            case 'authenticated': return 2;
            default: return 0;
        }
    };

    const currentStepIndex = getCurrentStepIndex();

    return (
        <div className="w-full max-w-3xl mx-auto mb-8">
            <div className="relative flex justify-between items-center">
                {/* Progress Bar Background */}
                <div className="absolute top-1/2 left-0 right-0 h-1 bg-slate-800 -z-10 rounded-full" />

                {/* Active Progress Bar */}
                <motion.div
                    className="absolute top-1/2 left-0 h-1 bg-gradient-to-r from-cyan-500 to-indigo-500 -z-10 rounded-full"
                    initial={{ width: '0%' }}
                    animate={{ width: `${(currentStepIndex / (steps.length - 1)) * 100}%` }}
                    transition={{ duration: 0.5, ease: "easeInOut" }}
                />

                {steps.map((step, index) => {
                    const isActive = index <= currentStepIndex;
                    const isCompleted = index < currentStepIndex;
                    const isCurrent = index === currentStepIndex;

                    return (
                        <div key={step.id} className="flex flex-col items-center gap-2">
                            <motion.div
                                className={`w-10 h-10 rounded-full flex items-center justify-center border-2 transition-all duration-300 z-10
                                    ${isActive
                                        ? 'bg-black border-cyan-500 text-cyan-400 shadow-[0_0_15px_rgba(6,182,212,0.4)]'
                                        : 'bg-black border-slate-700 text-slate-600'
                                    }
                                `}
                                animate={{ scale: isCurrent ? 1.1 : 1 }}
                            >
                                {isCompleted ? <Check size={18} /> : <step.icon size={18} />}
                            </motion.div>
                            <span className={`text-xs font-semibold tracking-wide uppercase transition-colors duration-300 ${isActive ? 'text-cyan-400' : 'text-slate-600'}`}>
                                {step.label}
                            </span>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
