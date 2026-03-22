import type { CardType } from "../modules/GameContext";
import  { type CardSizeType } from "./Card";
import DragableCard from "./game/DragableCard";


export default function Cards({
    isInline = true,
    isRotated = true,
    cards,
    onClickCard,
    isAnimated = true,
    size = "NORMAL",
}: {isRotated?: boolean,
    isAnimated?:boolean,
    cards:Array<CardType>,
    size?:CardSizeType,
    isInline?: boolean
    onClickCard?:{(e: React.MouseEvent<HTMLDivElement>): void}},
   
) {
    if(cards === undefined || cards.length === 0 || cards === null){
        return;
    }
    return (
        <div style={{display: "flex",
            flexDirection:"row",
            alignItems: "center",
            marginLeft: "5em"
            }}>
            {cards.map(card => (
            <DragableCard
                {...(onClickCard ? { onClick: onClickCard } : {})}
                image={"/img/karty/" + card.image + ".png"}
                style={{maxWidth: 100/(cards.length-10)+"vw"}}
                key={card.id}
                id={card.id}
                animationOnStart={isAnimated}
                biggerOnHover={false}
                isInLine={isInline}
                isRotated={isRotated}
                size={size}
            />
            ))}
        </div>
    );
}