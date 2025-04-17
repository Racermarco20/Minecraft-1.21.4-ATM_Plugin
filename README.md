# 🏧 ATM-Plugin

Ein Minecraft-Plugin zur Simulation eines modernen Bankautomaten mit PIN-Eingabe und einer virtuellen Währungen (RedBits & RedBytes).

## 📦 Features

- ✅ PIN-gesicherter Zugang mit Keycard (Name Tag)
- 💸 Zwei virtuelle Währungen:
  - **RedBit** (Gold Nugget)
  - **RedByte** (Emerald, 1 Byte = 8 Bits)
- 💰 Einzahlen und Abheben
- 🔄 Konvertieren von RedBits ⇄ RedBytes
- 📊 Kontostand abfragen
- 🖼 GUI mit eigenem ATM-Design (ArmorStands)
- 🧠 Spieler-PINs werden sicher in YAML-Dateien gespeichert

## 🧪 Nutzung

1. Platziere einen unsichtbaren ArmorStand mit dem Tag `ATM-Machines` an deinem ATM-Ort:
```command
/summon armor_stand ~ ~ ~ {Invisible:1b,Invulnerable:1b,NoGravity:1b,Tags:["ATM-Machines"],CustomName:'"ATM-Machines"',CustomNameVisible:0b}
```

2. Gehe in die Nähe des ATMs. Drücke einen Knopf (Stone). Wenn erkannt, öffnet sich das PIN-Interface.
3. Lege deine **Keycard** (Name Tag) in Slot 4.
4. Gib deine PIN ein (z. B. 1234).
5. Nach erfolgreicher Verifikation wird das Hauptmenü geöffnet.

## 🪙 Währungen

| Item         | Bedeutung     | Material     |
|--------------|---------------|--------------|
| RedBit       | 1 Bit         | Gold Nugget  |
| RedByte      | 8 Bits        | Emerald      |

## 🛠 Setup

1. Baue das Plugin mit Maven oder lade das fertige JAR in den `plugins/` Ordner.
2. Starte den Server.
3. Die PIN-Daten werden automatisch in `/plugins/ATMPlugin/pin-data.yml` gespeichert.

## 🚀 Kommandos

| Befehl         | Beschreibung                  |
|----------------|-------------------------------|
| `/atm`         | Öffnet das ATM (sofern Nähe zu ATM-Machine erkannt wird) |
| `/atm setpin`  | (optional) Setze deine PIN (noch nicht implementiert)   |

## 📁 Struktur

- `ATMGui.java` – Hauptmenü-GUI
- `ATMPINClickListener.java` – PIN-Logik
- `ATMClickListener.java` – ATM-Klick-Logik
- `ATMManager.java` – Währungs- und Banklogik
- `BankAccount.java` – Scoreboard-basiertes Bankkonto
- `plugin.yml` – Bukkit Plugin Konfiguration

## ❤️ Autor

Erstellt von **racermarco20**  
Lizenz: Frei verwendbar für private Projekte.
