import { useDroppable } from "@dnd-kit/core";
import { useRef, useEffect, useCallback, useState } from "react";


export function Deck({ images }: { images: string[] }) {
    const canvasRef = useRef<HTMLCanvasElement>(null);
    const deckHistoryRef = useRef<Array<{ img: HTMLImageElement; dx: number; dy: number; deg: number }>>([]);
    const { setNodeRef, isOver } = useDroppable({ id: "discardPile" });
    const [dimensions, setDimensions] = useState({ width: 300, height: 300 });

    // Pomocný ref pro uložení poslední zpracované délky pole, abychom reagovali jen na reálné změny
    const lastLengthRef = useRef(0);

    const nahodneCislo = (min: number, max: number) => {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    };

    const vykresliKartu = useCallback((
        ctx: CanvasRenderingContext2D,
        img: HTMLImageElement,
        canvasW: number,
        canvasH: number,
        dx: number,
        dy: number,
        deg: number
    ) => {
        const origW = img.naturalWidth || img.width;
        const origH = img.naturalHeight || img.height;
        
        const targetW = canvasW * 0.35; 
        const targetH = origH * (targetW / origW);
        
        const cx = canvasW / 2;
        const cy = canvasH / 2;
        const rad = (deg * Math.PI) / 180;

        const realneDx = dx * (canvasW / 300);
        const realneDy = dy * (canvasH / 300);

        ctx.save();
        ctx.translate(cx + realneDx, cy + realneDy);
        ctx.rotate(rad);

        ctx.shadowColor = "rgba(0, 0, 0, 0.4)";
        ctx.shadowOffsetX = -2 * (canvasW / 300); 
        ctx.shadowOffsetY = 2 * (canvasH / 300);
        ctx.shadowBlur = 4 * (canvasW / 300);

        const r = 8 * (canvasW / 300); 
        const x = -targetW / 2;
        const y = -targetH / 2;
        const w = targetW;
        const h = targetH;

        ctx.beginPath();
        if (typeof ctx.roundRect === "function") {
            ctx.roundRect(x, y, w, h, r);
        } else {
            ctx.moveTo(x + r, y);
            ctx.lineTo(x + w - r, y);
            ctx.quadraticCurveTo(x + w, y, x + w, y + r);
            ctx.lineTo(x + w, y + h - r);
            ctx.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
            ctx.lineTo(x + r, y + h);
            ctx.quadraticCurveTo(x, y + h, x, y + h - r);
            ctx.lineTo(x, y + r);
            ctx.quadraticCurveTo(x, y, x + r, y);
        }
        ctx.closePath();
        ctx.clip();
        ctx.drawImage(img, x, y, w, h);
        ctx.restore();
    }, []);

    const prekresliVsechno = useCallback(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        const ctx = canvas.getContext("2d");
        if (!ctx) return;

        const dpr = window.devicePixelRatio || 1;
        
        canvas.width = dimensions.width * dpr;
        canvas.height = dimensions.height * dpr;
        
        ctx.resetTransform();
        ctx.scale(dpr, dpr);
        ctx.clearRect(0, 0, dimensions.width, dimensions.height);

        deckHistoryRef.current.forEach((karta) => {
            vykresliKartu(ctx, karta.img, dimensions.width, dimensions.height, karta.dx, karta.dy, karta.deg);
        });
    }, [dimensions, vykresliKartu]);

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;

        const resizeObserver = new ResizeObserver((entries) => {
            for (const entry of entries) {
                const { width, height } = entry.contentRect;
                setDimensions({ width: width || 300, height: height || 300 });
            }
        });

        resizeObserver.observe(canvas);
        return () => resizeObserver.disconnect();
    }, []);

    useEffect(() => {
        prekresliVsechno();
    }, [dimensions, prekresliVsechno]);

    // 3. Efekt, který reaguje POUZE na přidání nové karty do pole `images`
    useEffect(() => {
        if (images.length === 0) {
            deckHistoryRef.current = [];
            lastLengthRef.current = 0;
            prekresliVsechno();
            return;
        }

        // Pokud se délka pole nezvětšila, ignorujeme to (zabraňuje skákání při resize)
        if (images.length <= lastLengthRef.current) {
            return;
        }

        const raw = images[images.length - 1];
        if (!raw || typeof raw !== "string") return;

        const src = raw.startsWith("/") || raw.startsWith("http") ? raw : `/img/karty/${raw}.png`;
        
        const img = new window.Image();
        img.src = src;
        img.onload = () => {
            const novaKarta = {
                img,
                dx: nahodneCislo(-30, 30), 
                dy: nahodneCislo(-30, 30),
                deg: nahodneCislo(-30, 30) 
            };

            deckHistoryRef.current.push(novaKarta);
            
            if (deckHistoryRef.current.length > 20) {
                deckHistoryRef.current.shift();
            }

            lastLengthRef.current = images.length;
            prekresliVsechno();
        };
    }, [images]); // Záměrně odebráno 'prekresliVsechno' ze závislostí

    return (
        <canvas
            ref={(node) => {
                canvasRef.current = node;
                setNodeRef(node); 
            }}
            style={{ 
                width: "100%",
                maxWidth: "clamp(6.5em, 30vw, 300px)", 
                aspectRatio: "1 / 1",
                outline: isOver ? "2px solid yellow" : undefined,
                boxSizing: "border-box"
            }}
        />
    );
}