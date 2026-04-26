import { useEffect, useRef } from 'react';

interface AudioPlayerProps {
    stream: MediaStream;
    playerName?: string;
}

/**
 * Komponenta pro přehrávání audio streamu z jednoho hráče
 * Používá HTML5 <audio> element na pozadí (neviditelný)
 */
export default function AudioPlayer({ stream, playerName }: AudioPlayerProps) {
    const audioRef = useRef<HTMLAudioElement>(null);

    useEffect(() => {
        if (!audioRef.current || !stream) return;

        const audioElement = audioRef.current;

        // Připoj stream k audio elementu
        audioElement.srcObject = stream;

        // Automaticky spusť přehrávání
        audioElement.play().catch(err => {
            console.log(`Audio play failed for ${playerName}:`, err);
        });

        return () => {
            // Cleanup
            audioElement.srcObject = null;
        };
    }, [stream, playerName]);

    // Audio element je neviditelný - jen přehrává zvuk
    return (
        <audio
            ref={audioRef}
            autoPlay
            playsInline
            style={{ display: 'none' }}
        />
    );
}
