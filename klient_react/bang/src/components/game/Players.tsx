import Player from "./Player";
import { useRef, useEffect } from "react";
import css from "../../styles/scrolable.module.css"

export default function Players() {
    const containerRef = useRef<HTMLDivElement>(null);

    // Přesměrování vertikálního scrollu na horizontální
    function handleWheel(e: React.WheelEvent<HTMLDivElement>) {
        if (containerRef.current) {
            containerRef.current.scrollTo({
                left: containerRef.current.scrollLeft + e.deltaY * 5,
                behavior: "smooth"
            });
            e.preventDefault();
        }
    }

    useEffect(() => {
        if (containerRef.current) {
            containerRef.current.scrollLeft = 500;
        }
    }, []);

    return (
        <div
            ref={containerRef}
            className={css.scrollable}
            style={{
                flexShrink: 0,
                flexDirection: "row",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                padding: "0 10px",
                width: "100%",
                overflowX: "auto",
                overflowY: "hidden",
                whiteSpace: "nowrap",
            }}
            onWheel={handleWheel}
        >
            <div style={{ width: 500, flexShrink: 0 }} />
            <Player jmeno="honza" />
            <Player jmeno="Někdo, kdo má hodně dlouhé jméno" />
            <Player jmeno="František" />
            <Player jmeno="Pepa" />
            <Player jmeno="Karel" />
            <Player jmeno="Jirka" />
            <Player jmeno="Martin" />
            <Player jmeno="Tonda" />
            <div style={{ width: 500, flexShrink: 0 }} />
        </div>
    );
}