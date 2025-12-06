import { useDroppable } from "@dnd-kit/core";
import Cards from "../Cards";
import type { CardType } from "../../modules/GameContext";

export default function InPlayCards({vylozeneKarty}: {vylozeneKarty: CardType[]}) {
    const { setNodeRef, isOver } = useDroppable({ id: "InPlayCards" });

    console.log("InPlayCards render ", isOver);
    return (
        <div style={{ position: "relative", border: isOver ? "1px solid yellow" : "none", minHeight: "100px", minWidth: "100px" }} >
            <Cards isAnimated={false} isRotated={false} onClickCard={alert} cards={vylozeneKarty}/>
            <div
                ref={setNodeRef}
                style={{
                position: "absolute",
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                borderRadius: 6,
                background: isOver ? "rgba(255,255,0,0.12)" : "transparent",
                border: isOver ? "2px dashed yellow" : "1px dashed rgba(0,0,0,0.08)",
                pointerEvents: "none",
                zIndex: 10,
                height:"50%",
                }}
            />
        </div>
    );
}