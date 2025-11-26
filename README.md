# Bingo Bot for Discord

![Bingo Card Table](/card_example.png)

## Commands

- **`/bingo-start`** - Starts a new bingo round! This grabs messages from a designated channel (called `pool` by default) and randomly picks 25 items to fill the bingo cards.
- **`/bingo-join`** - Sign up for the current round and generate your bingo card!
- **`/bingo-card`** - View your bingo card while you're in the game.
- **`/bingo-check A1`** - Mark the specified cell (e.g., A1) as complete.
- **`/bingo-uncheck A1`** - Undo a mark on your bingo card (perfect for those accidental checks).
- **`/bingo-source [channel name]`** - Set a specific channel as the source for bingo items (default is `pool`).
- **`/bingo-pool-size [pool size]`** - Set the size of the round pool. Must be at least 25. If there are fewer items in the pool channel it will just use them all.

## How to Use

### 1. Setting Up the Pool

Before you can play, you need to fill up the pool with items for your bingo cards. These items will come from messages in a designated channel (default: `pool`).

To set the channel, type:

`/bingo-source [channel name]`


Then, add your items to the channel. You can do this in a few ways:

- **One item per message:**
```
item 1
```
- **Multiple items at once:**
```
item 1
item 2
item 3
```
- **Paste a JSON array:**
```
[
    "item 1",
    "item 2",
    "item 3"
]
```
Make sure there are **at least 25 items** in the pool to generate a complete bingo card!

**Tip:** You can specify the number of items loaded for each round with the `/bingo-pool-size` command.

### 2. Starting a Round

Once you’ve got your pool filled with items, it's time to kick off a round:

Start the round: `/bingo-start`

The bot will randomly select the specified number of items from your pool and get the round going.

### 3. Playing the game
Once the round's started everything is ready for you to join the round.

`/bingo-join` This will generate your card from the current round item pool.<br>
`/bingo-card` If you want to bring your card up again. 

Then you can use:<br>
`/bingo-check [cell coords e.g. A1]` to mark a complete cell.<br>
`/bingo-uncheck [cell coords e.g. A1]` to fix any mistakes when doing so.

Enjoy the game and may your horrible luck bring you victory!