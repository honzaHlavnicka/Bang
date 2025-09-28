import { useGame } from "../modules/GameContext";
import Card from "./Card";

export default function CharacterPicker() {
    const {gameState, chooseCharacter} = useGame();

    if(!gameState.characters){
        return <div>Načítání postav...</div>
    }
    if(gameState.characters.length === 0){
        return;
    }

    function vybrano(character:string){
        chooseCharacter(character);
    }
    return (
        <div style={{textAlign:"center",padding:"10px",backgroundColor:"rgba(0,0,0,0.5)",borderRadius:"10px",color:"white"}}>

        <h3>vyber si jednu z těchto postav</h3>
        <div style={{display:"flex",gap:"10px",justifyContent:"center",flexWrap:"wrap"}}>
            {gameState.characters!.map((character, idx) => (
            <Card
                key={character.obrazek + "_" + idx}
                name={character.jmeno + ":\n " + character.popis}
                image={"/img/karty/postavy/" + character.obrazek + ".png"}
                biggerOnHover
                onClick={() => vybrano(character.obrazek)}
            />
            ))}
        </div>
        </div>
    );
}