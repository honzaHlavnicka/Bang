import type { CardType } from "../modules/GameContext";
import Card, { type CardSizeType } from "./Card";


export default function Cards({
    isRotated = true,
    cards,
    onClickCard,
    isAnimated = true,
    size = "NORMAL"
}: {isRotated?: boolean,
    isAnimated?:boolean,
    cards:Array<CardType>,
    size:CardSizeType,
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
            <Card
                {...(onClickCard ? { onClick: onClickCard } : {})}
                image={"/img/karty/" + card.image + ".png"}
                key={card.id}
                id={card.id}
                animationOnStart={isAnimated}
                biggerOnHover
                isInLine
                isRotated={isRotated}
                size={size}
            />
            ))}
        </div>
    );
}