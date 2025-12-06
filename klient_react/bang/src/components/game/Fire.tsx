import { useDroppable } from "@dnd-kit/core";

export default function Fire() {
    const { setNodeRef, isOver } = useDroppable({ id: "fire" });

    return (
        <div ref={setNodeRef} style={{ maxWidth: "10em", maxHeight: "10em", border:isOver?"1px solid yellow":"none"}} >
            <img style={{width:"100%",mixBlendMode: "multiply"}}  src="/img/ui/fire.gif" alt="fire animation" />
        </div>
    );
}