import type { CardType } from "../modules/GameContext";
import Card from "./Card";


export default function Cards({isRotated = true,cards,onClickCard}: {isRotated?: boolean,cards:Array<CardType>,onClickCard?:{(e: React.MouseEvent<HTMLDivElement>): void}}) {
    console.log(cards)
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
                animationOnStart
                biggerOnHover
                isInLine
                isRotated={isRotated}
            />
            ))}
        </div>
    );
}