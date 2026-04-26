import { useState, useEffect } from 'react';
import { useGame } from '../modules/GameContext';
import { voiceManager } from '../voice/voiceManager';
import toast from 'react-hot-toast';
import globalCSS from "../styles/global.module.css";
import AudioPlayer from './AudioPlayer';

export default function VoiceChatWidget() {
    const { gameState, voiceInit, voiceToggleMute, voiceDisconnect } = useGame();
    const [isVoiceConnected, setIsVoiceConnected] = useState(false);
    const [isMuted, setIsMuted] = useState(false);
    const [connectedUsers, setConnectedUsers] = useState<number>(0);
    const [remoteStreams, setRemoteStreams] = useState<Map<string, MediaStream>>(new Map());

    // Sleduj změny v remoteStreams z voiceManager
    useEffect(() => {
        const interval = setInterval(() => {
            setRemoteStreams(new Map(voiceManager.remoteStreams));
        }, 500); // Kontroluj každých 500ms

        return () => clearInterval(interval);
    }, []);

    // Inicializace voice chatu
    const handleVoiceInit = async () => {
        if (!gameState.playerId || gameState.playerId === 0) {
            toast.error('Nemáš player ID');
            return;
        }

        try {
            const userId = String(gameState.playerId);
            
            await voiceInit(
                userId,
                // onUpdate callback - když se změní stav streamů
                () => {
                    setConnectedUsers(voiceManager.remoteStreams.size);
                    setRemoteStreams(new Map(voiceManager.remoteStreams));
                },
                // sendMessage callback - pro odesílání zpráv serveru
                (msg: string) => {
                    const ws = (window as unknown as { ws?: WebSocket }).ws;
                    if (ws && ws.readyState === WebSocket.OPEN) {
                        ws.send(msg);
                    } else {
                        toast.error('Není připojení k serveru');
                    }
                }
            );

            setIsVoiceConnected(true);
            toast.success('Připojeno k voice chatu!');
        } catch (err) {
            toast.error('Nepodařilo se připojit k voice chatu');
            console.error('Voice init error:', err);
        }
    };

    const handleDisconnect = () => {
        voiceDisconnect();
        setIsVoiceConnected(false);
        setConnectedUsers(0);
        setIsMuted(false);
        toast.success('Odpojen od voice chatu');
    };

    const handleMuteToggle = () => {
        const newMutedState = !isMuted;
        voiceToggleMute(newMutedState);
        setIsMuted(newMutedState);
        
        // Pošli serveru informaci o ztlumení
        const ws = (window as unknown as { ws?: WebSocket }).ws;
        if (ws && ws.readyState === WebSocket.OPEN && gameState.playerId) {
            ws.send(`voicechat:ztlumeni:${newMutedState}`);
        }
    };

    if (!gameState.inGame) {
        return null;
    }

    return (
        <>
            {/* Audio playery pro všechny remote streamy */}
            {Array.from(remoteStreams.entries()).map(([userId, stream]) => (
                <AudioPlayer 
                    key={userId} 
                    stream={stream} 
                    playerName={`Player ${userId}`}
                />
            ))}
            
            <div style={{
                position: 'fixed',
                bottom: 20,
                right: 20,
                zIndex: 100,
                display: 'flex',
                flexDirection: 'column',
                gap: 8,
                alignItems: 'flex-end'
            }}>
                {isVoiceConnected && (
                    <div style={{
                        background: 'rgba(0, 0, 0, 0.8)',
                        color: 'white',
                        padding: '8px 12px',
                        borderRadius: 8,
                        fontSize: '12px',
                        minWidth: '120px'
                    }}>
                        <div>🎤 Připojeni: {connectedUsers}</div>
                        <div style={{ marginTop: 4, opacity: 0.8 }}>
                            {isMuted ? '🔇 Ztlumeno' : '🔊 Slyšet'}
                        </div>
                    </div>
                )}

                <div style={{ display: 'flex', gap: 8 }}>
                    {isVoiceConnected && (
                        <>
                            <button 
                                className={globalCSS.button}
                                onClick={handleMuteToggle}
                                title={isMuted ? 'Zapnout mikrofon' : 'Vypnout mikrofon'}
                                style={{
                                    background: isMuted ? '#ef4444' : '#10b981',
                                    padding: '8px 12px',
                                    fontSize: '14px'
                                }}
                            >
                                {isMuted ? '🔇' : '🎤'}
                            </button>
                            <button 
                                className={globalCSS.button}
                                onClick={handleDisconnect}
                                title="Odpojit se od voice chatu"
                                style={{
                                    background: '#6366f1',
                                    padding: '8px 12px',
                                    fontSize: '14px'
                                }}
                            >
                                ❌
                            </button>
                        </>
                    )}

                    {!isVoiceConnected && (
                        <button 
                            className={globalCSS.button}
                            onClick={handleVoiceInit}
                            title="Připojit se k voice chatu"
                            style={{
                                background: '#3b82f6',
                                padding: '8px 12px',
                                fontSize: '14px'
                            }}
                        >
                            🎙️ Voice Chat
                        </button>
                    )}
                </div>
            </div>
        </>
    );
}
