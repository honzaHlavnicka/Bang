import Card from "./Card";

interface Card {
    img: string;
    key: number;
}

const cards: Card[] = [
    { img: "bang", key: 27 },
    { img: "pivo", key: 29 },
    { img: "barel", key: 30 },
    { img: "wellsfargo", key: 31 },
];

export default function Cards() {
    return (
        <div style={{display: "flex",
            flexDirection:"row",
            alignItems: "center"}}>
            {cards.map(card => (
                <Card image={"/img/karty/" + card.img + ".png"} key={card.key} animationOnStart biggerOnHover isInLine />
            ))}
        </div>
    );
}