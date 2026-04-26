import { useGame } from "../modules/GameContext";
import Card from "./Card";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";

export default function CharacterPicker() {
    const {gameState, chooseCharacter} = useGame();
    const {t} = useTranslation();

    // Obsluha klávesových zkratek pro výběr postav (1 a 2)
    useEffect(() => {
        if (!gameState.characters || gameState.characters.length === 0) {
            return;
        }

        const handleKeyDown = (e: KeyboardEvent) => {
            // Ignoruj klávesy pokud je fokus na inputu, textareaě nebo selectu
            if (document.activeElement?.tagName === "INPUT" || 
                document.activeElement?.tagName === "TEXTAREA" ||
                document.activeElement?.tagName === "SELECT") {
                return;
            }

            const key = e.key;
            // Podporuj jak čísla z hlavní klávesnice, tak z numerické klávesnice
            if (key === "1" && gameState.characters && gameState.characters.length >= 1) {
                e.preventDefault();
                chooseCharacter(gameState.characters[0].obrazek);
            } else if (key === "2" && gameState.characters && gameState.characters.length >= 2) {
                e.preventDefault();
                chooseCharacter(gameState.characters[1].obrazek);
            }
        };

        window.addEventListener("keydown", handleKeyDown);
        return () => window.removeEventListener("keydown", handleKeyDown);
    }, [gameState.characters, chooseCharacter]);

    if(!gameState.characters){
        return <div>{t("Načítání postav...")}</div>
    }
    if(gameState.characters.length === 0){
        return;
    }

    function vybrano(character:string){
        chooseCharacter(character);
    }
    
    return (
        <div style={{textAlign:"center",padding:"10px",backgroundColor:"rgba(0,0,0,0.5)",borderRadius:"10px",color:"white"}}>

        <h3>{t("vyber si jednu z těchto postav")}</h3>
        <div style={{display:"flex",gap:"10px",justifyContent:"center",flexWrap:"wrap"}}>
            {gameState.characters!.map((character, idx) => (
            <div key={character.obrazek + "_" + idx} style={{position:"relative"}}>
                <Card
                    name={character.jmeno + ":\n " + character.popis}
                    image={"/img/karty/postavy/" + character.obrazek + ".png"}
                    biggerOnHover
                    onClick={() => vybrano(character.obrazek)}
                />
                <div style={{position:"absolute",top:"5px",left:"5px",backgroundColor:"rgba(0,0,0,0.7)",color:"white",padding:"3px 8px",borderRadius:"4px",fontSize:"12px",fontWeight:"bold"}}>
                    {idx + 1}
                </div>
            </div>
            ))}
        </div>
        </div>
    );
}