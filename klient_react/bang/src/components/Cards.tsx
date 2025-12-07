import type { CardType } from "../modules/GameContext";
import  { type CardSizeType } from "./Card";
import DragableCard from "./game/DragableCard";


export default function Cards({
    isRotated = true,
    cards,
    onClickCard,
    isAnimated = true,
    size = "NORMAL"
}: {isRotated?: boolean,
    isAnimated?:boolean,
    cards:Array<CardType>,
    size?:CardSizeType,
    onClickCard?:{(e: React.MouseEvent<HTMLDivElement>): void}}
) {
    if(cards === undefined || cards.length === 0 || cards === null){
        return;
    }
    return (
        <div style={{display: "flex",
            flexDirection:"row",
            alignItems: "center"}}>
            {cards.map(card => (
            <DragableCard
                {...(onClickCard ? { onClick: onClickCard } : {})}
                image={"/img/karty/" + card.image + ".png"}
                style={{maxWidth: 100/(cards.length-10)+"vw"}}
                key={card.id}
                id={card.id}
                animationOnStart={isAnimated}
                biggerOnHover={false}
                isInLine
                isRotated={isRotated}
                size={size}
            />
            ))}
        </div>
    );
}