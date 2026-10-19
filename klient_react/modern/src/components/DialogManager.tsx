import React, { useState, useEffect } from "react";
import { useDialog, type CardType, translateServerText, notifications } from "@bang/core";
import { ModalFrame, Button, CardView, Input, Badge } from "../design-system";
import { LuckyWheel } from "./LuckyWheel";
import { Check, User, Sparkles } from "lucide-react";

export const DialogManager: React.FC = () => {
  const { dialog, closeDialog } = useDialog();
  const [selectedCards, setSelectedCards] = useState<number[]>([]);
  const [selectedPlayers, setSelectedPlayers] = useState<number[]>([]);
  const [textValue, setTextValue] = useState("");
  const [wheelFinished, setWheelFinished] = useState(false);

  // Reset při změně dialogu
  useEffect(() => {
    setSelectedCards([]);
    setSelectedPlayers([]);
    setWheelFinished(false);
    if (dialog?.type === "TEXT") {
      setTextValue(dialog.data.defaultValue || "");
    } else {
      setTextValue("");
    }
  }, [dialog]);

  if (!dialog) return null;

  // 1. SELECT_CARD
  if (dialog.type === "SELECT_CARD") {
    const { cards, min, max } = dialog.data;
    const canConfirm = selectedCards.length >= min && selectedCards.length <= max;

    const toggleCard = (id: number) => {
      if (selectedCards.includes(id)) {
        setSelectedCards(selectedCards.filter((c) => c !== id));
      } else {
        if (selectedCards.length < max) {
          setSelectedCards([...selectedCards, id]);
        } else if (max === 1) {
          setSelectedCards([id]);
        }
      }
    };

    const handleConfirm = () => {
      if (!canConfirm) return;
      const result = [...selectedCards];
      closeDialog();
      dialog.callback?.(result);
    };

    return (
      <ModalFrame
        isOpen={true}
        onClose={closeDialog}
        notClosable={dialog.notClosable}
        title={dialog.dialogHeader ? translateServerText(dialog.dialogHeader) : "Vyber kartu"}
        subtitle={`Vyber ${min === max ? min : `${min} až ${max}`} ${max === 1 ? "kartu" : max < 5 ? "karty" : "karet"}`}
        maxWidth="lg"
      >
        <div className="flex flex-wrap justify-center gap-5 sm:gap-7 p-4 sm:p-6 max-h-[58vh] overflow-y-auto">
          {cards.map((card: CardType) => {
            const isSel = selectedCards.includes(card.id);
            return (
              <div
                key={card.id}
                onClick={() => toggleCard(card.id)}
                className="p-1 relative cursor-pointer transition-transform hover:scale-105 active:scale-95"
              >
                <CardView
                  image={card.image}
                  name={translateServerText(card.name)}
                  size="md"
                  isSelected={isSel}
                  isPlayable={true}
                />
              </div>
            );
          })}
        </div>
        <div className="flex justify-end gap-2 pt-3 border-t border-[#382f28]">
          {!dialog.notClosable && (
            <Button variant="ghost" onClick={closeDialog}>
              Zrušit
            </Button>
          )}
          <Button variant="primary" disabled={!canConfirm} onClick={handleConfirm}>
            Potvrdit výběr ({selectedCards.length}/{max})
          </Button>
        </div>
      </ModalFrame>
    );
  }

  // 2. SELECT_PLAYER
  if (dialog.type === "SELECT_PLAYER") {
    const { players, min, max } = dialog.data;
    const canConfirm = selectedPlayers.length >= min && selectedPlayers.length <= max;

    const togglePlayer = (id: number) => {
      if (selectedPlayers.includes(id)) {
        setSelectedPlayers(selectedPlayers.filter((p) => p !== id));
      } else {
        if (selectedPlayers.length < max) {
          setSelectedPlayers([...selectedPlayers, id]);
        } else if (max === 1) {
          setSelectedPlayers([id]);
        }
      }
    };

    const handleConfirm = () => {
      if (!canConfirm) return;
      const result = [...selectedPlayers];
      closeDialog();
      dialog.callback(result);
    };

    return (
      <ModalFrame
        isOpen={true}
        onClose={closeDialog}
        notClosable={dialog.notClosable}
        title={dialog.dialogHeader ? translateServerText(dialog.dialogHeader) : "Vyber hráče"}
        subtitle={`Zvol ${min === max ? min : `${min} až ${max}`} ${max === 1 ? "hráče" : "hráče"}`}
        maxWidth="md"
      >
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5 py-2">
          {players.map((p) => {
            const isSel = selectedPlayers.includes(p.id);
            return (
              <button
                key={p.id}
                onClick={() => togglePlayer(p.id)}
                className={`flex items-center justify-between p-3 rounded-xl border text-left transition-all cursor-pointer ${
                  isSel
                    ? "bg-amber-600/20 border-amber-500 text-[#f5ede3] shadow-md ring-1 ring-amber-500"
                    : "bg-[#2d2520] border-[#423830] text-[#b5a596] hover:text-[#f5ede3] hover:border-[#54463c]"
                }`}
              >
                <div className="flex items-center gap-2.5">
                  <div
                    className={`w-8 h-8 rounded-full flex items-center justify-center ${
                      isSel ? "bg-amber-500 text-black font-bold" : "bg-[#1c1714] text-[#b5a596]"
                    }`}
                  >
                    <User className="w-4 h-4" />
                  </div>
                  <span className="font-semibold text-sm">{p.name}</span>
                </div>
                {isSel && <Check className="w-4 h-4 text-amber-400" />}
              </button>
            );
          })}
        </div>
        <div className="flex justify-end gap-2 pt-3 border-t border-[#382f28]">
          {!dialog.notClosable && (
            <Button variant="ghost" onClick={closeDialog}>
              Zrušit
            </Button>
          )}
          <Button variant="primary" disabled={!canConfirm} onClick={handleConfirm}>
            Potvrdit ({selectedPlayers.length}/{max})
          </Button>
        </div>
      </ModalFrame>
    );
  }

  // 3. CONFIRM_ACTION
  if (dialog.type === "CONFIRM_ACTION") {
    const { actions, card } = dialog.data;

    return (
      <ModalFrame
        isOpen={true}
        onClose={closeDialog}
        notClosable={dialog.notClosable}
        title={dialog.dialogHeader ? translateServerText(dialog.dialogHeader) : "Vyber akci"}
        maxWidth="sm"
      >
        {card && (
          <div className="flex justify-center mb-3">
            <CardView image={card.image} name={translateServerText(card.name)} size="md" />
          </div>
        )}
        <div className="space-y-2 py-1">
          {actions.map((act) => (
            <Button
              key={act.id}
              variant="primary"
              className="w-full justify-center text-sm py-2.5"
              onClick={() => {
                closeDialog();
                dialog.callback?.(act.id);
              }}
            >
              {translateServerText(act.name)}
            </Button>
          ))}
        </div>
      </ModalFrame>
    );
  }

  // 4. INFO
  if (dialog.type === "INFO") {
    const { header, message, image } = dialog.data;
    return (
      <ModalFrame
        isOpen={true}
        onClose={closeDialog}
        notClosable={dialog.notClosable}
        title={dialog.dialogHeader ? translateServerText(dialog.dialogHeader) : header ? translateServerText(header) : "Informace"}
        maxWidth="md"
      >
        {image && (
          <div className="flex justify-center mb-3">
            <img src={image} alt="Info" className="max-h-40 rounded-xl object-contain" />
          </div>
        )}
        <div className="text-sm text-[#f5ede3] leading-relaxed whitespace-pre-line py-1">
          {translateServerText(message)}
        </div>
        <div className="flex justify-end pt-3 border-t border-[#382f28]">
          <Button variant="primary" onClick={closeDialog}>
            Rozumím
          </Button>
        </div>
      </ModalFrame>
    );
  }

  // 5. CONFIRM
  if (dialog.type === "CONFIRM") {
    const { message, title, confirmText, cancelText, hideCancel } = dialog.data;
    return (
      <ModalFrame
        isOpen={true}
        onClose={closeDialog}
        notClosable={dialog.notClosable}
        title={title ? translateServerText(title) : "Potvrzení"}
        maxWidth="sm"
      >
        {message && (
          <p className="text-sm text-[#f5ede3] leading-relaxed py-1">
            {translateServerText(message)}
          </p>
        )}
        <div className="flex justify-end gap-2 pt-3 border-t border-[#382f28]">
          {!hideCancel && (
            <Button
              variant="ghost"
              onClick={() => {
                closeDialog();
                dialog.callback?.(false);
              }}
            >
              {cancelText ? translateServerText(cancelText) : "Zrušit"}
            </Button>
          )}
          <Button
            variant="primary"
            onClick={() => {
              closeDialog();
              dialog.callback?.(true);
            }}
          >
            {confirmText ? translateServerText(confirmText) : "Potvrdit"}
          </Button>
        </div>
      </ModalFrame>
    );
  }

  // 6. TEXT
  if (dialog.type === "TEXT") {
    const { title, placeholder, buttonText } = dialog.data;

    const handleSubmit = (e?: React.FormEvent) => {
      e?.preventDefault();
      closeDialog();
      dialog.callback?.(textValue.trim());
    };

    return (
      <ModalFrame
        isOpen={true}
        onClose={closeDialog}
        notClosable={dialog.notClosable}
        title={title ? translateServerText(title) : "Zadej text"}
        maxWidth="sm"
      >
        <form onSubmit={handleSubmit} className="space-y-4 py-1">
          <Input
            autoFocus
            value={textValue}
            onChange={(e) => setTextValue(e.target.value)}
            placeholder={placeholder ? translateServerText(placeholder) : "Napiš zde..."}
          />
          <div className="flex justify-end gap-2 pt-2">
            {!dialog.notClosable && (
              <Button type="button" variant="ghost" onClick={closeDialog}>
                Zrušit
              </Button>
            )}
            <Button type="submit" variant="primary">
              {buttonText ? translateServerText(buttonText) : "Potvrdit"}
            </Button>
          </div>
        </form>
      </ModalFrame>
    );
  }

  // 7. LUCKY_WHEEL
  if (dialog.type === "LUCKY_WHEEL") {
    const { options, chosedOptionId } = dialog.data;

    const handleConfirmWheel = () => {
      closeDialog();
      dialog.callback?.(chosedOptionId);
    };

    const handleSkipOrEarlyClose = () => {
      const chosenOption = options?.find((o: any) => o.id === chosedOptionId);
      const resultText = chosenOption ? translateServerText(chosenOption.name) : "";
      if (resultText) {
        notifications.success(`Kolo osudu: ${resultText}`);
      }
      closeDialog();
      dialog.callback?.(chosedOptionId);
    };

    return (
      <ModalFrame
        isOpen={true}
        onClose={handleSkipOrEarlyClose}
        notClosable={false}
        title={
          <div className="flex items-center gap-2">
            <Sparkles className="w-5 h-5 text-amber-400" />
            <span>Kolo osudu</span>
          </div>
        }
        subtitle="Probíhá vyhodnocení situace..."
        maxWidth="sm"
      >
        <LuckyWheel
          options={options}
          chosedOptionId={chosedOptionId}
          onConfirm={handleConfirmWheel}
          onSkip={handleSkipOrEarlyClose}
        />
      </ModalFrame>
    );
  }

  return null;
};

export default DialogManager;
