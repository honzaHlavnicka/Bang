import { useDraggable } from "@dnd-kit/core";
import Card, { type CardSizeType } from "../Card";

type CardProps = {
    image: string;
    name?: string;
    isRotated?: boolean;
    style?: React.CSSProperties;
    biggerOnHover?: boolean;
    animationOnStart?: boolean;
    isInLine?: boolean;
    id:number;
    onClick?: React.MouseEventHandler<HTMLDivElement>;
    size?:CardSizeType
};
export default function DragableCard({
    image,
    name = (image.match(/([^/\\]+)\.[^/\\]+$/)?.[1]) ?? image, //vrátí pouze jméno souboru. Pokud se soubor jmenuje nejak necekane, tak se vrátí celá cesta.
    isRotated = false,
    style,
    animationOnStart = false,
    biggerOnHover = false,
    isInLine = false,
    id,
    onClick,
}: CardProps) {
    const { attributes, listeners, setNodeRef, transform } = useDraggable({ id });

    return (
        <div  ref={setNodeRef} {...attributes} {...listeners} style={{ transform: transform ? `translate3d(${transform.x}px, ${transform.y}px, 0)` : undefined,touchAction: "none" }}>
            <Card image={image} name={name} isRotated={isRotated} style={style} animationOnStart={animationOnStart} biggerOnHover={biggerOnHover} isInLine={isInLine} onClick={onClick}/>
        </div>
    );
}