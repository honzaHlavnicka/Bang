import { useRef, useEffect } from "react";

export function Deck({ images }: { images: string[] }) {
    const canvasRef = useRef<HTMLCanvasElement>(null);
    const loadedImagesRef = useRef<HTMLImageElement[]>([]);

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        const ctx = canvas.getContext("2d");
        if (!ctx) return;

        const dpr = window.devicePixelRatio || 1;
        const cssWidth = 300;
        const cssHeight = 300;
        canvas.width = cssWidth * dpr;
        canvas.height = cssHeight * dpr;
        canvas.style.width = cssWidth + "px";
        canvas.style.height = cssHeight + "px";
        ctx.setTransform(1, 0, 0, 1, 0, 0);
        ctx.scale(dpr, dpr);
    }, []);

    useEffect(() => {
        if (images.length === 0) return;
        const canvas = canvasRef.current;
        if (!canvas) return;
        const ctx = canvas.getContext("2d");
        if (!ctx) return;

        const dpr = window.devicePixelRatio || 1;
        const src = images[images.length - 1]; // poslední karta
        const img = new window.Image();
        img.src = src;
        img.onload = () => {
            loadedImagesRef.current.push(img);
            vykresli(ctx, img, canvas, dpr);
        };
    }, [images]);

    function vykresli(
        ctx: CanvasRenderingContext2D,
        img: HTMLImageElement,
        canvas: HTMLCanvasElement,
        dpr: number
    ) {
        const origW = img.naturalWidth || img.width;
        const origH = img.naturalHeight || img.height;
        const targetW = 112;
        const targetH = origH * (targetW / origW);
        const cx = canvas.width / (2 * dpr);
        const cy = canvas.height / (2 * dpr);
        const dx = nahodneCislo(-30, 30);
        const dy = nahodneCislo(-30, 30);
        const deg = nahodneCislo(-30, 30);
        const rad = deg * Math.PI / 180;

        ctx.save();
        ctx.translate(cx + dx, cy + dy);
        ctx.rotate(rad);

        ctx.shadowColor = "black";
        ctx.shadowOffsetX = -2;
        ctx.shadowOffsetY = 2;
        ctx.shadowBlur = 2;

        const r = 8;
        const x = -targetW / 2;
        const y = -targetH / 2;
        const w = targetW;
        const h = targetH;

        ctx.beginPath();
        if ((ctx as any).roundRect) {
            (ctx as any).roundRect(x, y, w, h, r);
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
    }

    function nahodneCislo(min: number, max: number) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    return <canvas ref={canvasRef} width={300} height={300} style={{ width: 300, height: 300 }} />;
}
