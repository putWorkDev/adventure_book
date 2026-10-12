-- The four bundled books are installed by Flyway, not loaded from files at runtime.
-- Each JSON document occurs only once in this migration; the normalized rows
-- below are derived from it. source_json is deliberately not populated.
-- Empty dragon-quest.json is represented by NULL plus an UNPARSEABLE error.
CREATE TEMP TABLE seed_book (
    id VARCHAR(100) PRIMARY KEY,
    payload JSONB
) ON COMMIT DROP;

INSERT INTO seed_book (id, payload) VALUES
    ('crystal-caverns', $book$
{
  "title": "The Crystal Caverns",
  "author": "Evelyn Stormrider",
  "difficulty": "EASY",
  "sections": [
    {
      "id": 1,
      "text": "You stand at the entrance of the legendary Crystal Caverns. A cold breeze carries whispers from the darkness below. A broken rope bridge leads into the depths.",
      "type": "BEGIN",
      "options": [
        {
          "description": "Cross the rope bridge carefully",
          "gotoId": 100
        },
        {
          "description": "Search the rocky walls for another path",
          "gotoId": 20
        }
      ]
    },
    {
      "id": 20,
      "text": "Your hands brush against the cold stone until you find a hidden crevice leading downward. It's dark, but you hear faint dripping water.",
      "type": "NODE",
      "options": [
        {
          "description": "Enter the crevice",
          "gotoId": 200,
          "consequence": {
            "type": "LOSE_HEALTH",
            "value": "4",
            "text": "You scrape your shoulder squeezing through the narrow gap."
          }
        },
        {
          "description": "Return to the entrance",
          "gotoId": 1
        }
      ]
    },
    {
      "id": 100,
      "text": "The bridge creaks under your weight. Halfway across, a plank snaps beneath your foot!",
      "type": "NODE",
      "options": [
        {
          "description": "Hold on tightly and crawl across",
          "gotoId": 300
        },
        {
          "description": "Try to jump to the other side",
          "gotoId": 400,
          "consequence": {
            "type": "LOSE_HEALTH",
            "value": "7",
            "text": "You land hard and twist your ankle."
          }
        }
      ]
    },
    {
      "id": 200,
      "text": "You emerge into a glittering chamber filled with glowing crystals. At the center lies a pedestal holding an ancient gemstone.",
      "type": "NODE",
      "options": [
        {
          "description": "Take the gemstone",
          "gotoId": 500
        },
        {
          "description": "Inspect the walls carefully",
          "gotoId": 600
        }
      ]
    },
    {
      "id": 300,
      "text": "You crawl to safety on the other side, breathing heavily. A winding path descends deeper into the cavern.",
      "type": "NODE",
      "options": [
        {
          "description": "Follow the path downward",
          "gotoId": 600
        },
        {
          "description": "Rest and recover your strength",
          "gotoId": 700,
          "consequence": {
            "type": "GAIN_HEALTH",
            "value": "3",
            "text": "You feel slightly better after resting."
          }
        }
      ]
    },
    {
      "id": 400,
      "text": "You barely make the jump, but the rope bridge collapses behind you. There's no turning back now.",
      "type": "NODE",
      "options": [
        {
          "description": "Continue deeper into the cavern",
          "gotoId": 600
        }
      ]
    },
    {
      "id": 500,
      "text": "The moment you touch the gemstone, the chamber begins to shake violently. A secret door opens to reveal a hidden passage.",
      "type": "NODE",
      "options": [
        {
          "description": "Enter the hidden passage",
          "gotoId": 800
        },
        {
          "description": "Drop the gemstone and run back",
          "gotoId": 200
        }
      ]
    },
    {
      "id": 600,
      "text": "You reach a vast underground lake glowing with bioluminescent crystals. A small boat waits at the shore.",
      "type": "NODE",
      "options": [
        {
          "description": "Take the boat across the lake",
          "gotoId": 900
        },
        {
          "description": "Swim across",
          "gotoId": 1000,
          "consequence": {
            "type": "LOSE_HEALTH",
            "value": "5",
            "text": "The freezing water chills you to the bone."
          }
        }
      ]
    },
    {
      "id": 700,
      "text": "After a short rest, you feel strong enough to continue your journey.",
      "type": "NODE",
      "options": [
        {
          "description": "Head toward the underground lake",
          "gotoId": 600
        }
      ]
    },
    {
      "id": 800,
      "text": "The hidden passage leads to a giant crystal throne room, where an ancient spirit awaits.",
      "type": "END"
    },
    {
      "id": 900,
      "text": "The boat carries you safely across, but you sense something massive moving in the depths below.",
      "type": "NODE",
      "options": [
        {
          "description": "Ignore it and continue forward",
          "gotoId": 800
        },
        {
          "description": "Investigate the movement",
          "gotoId": 666
        }
      ]
    },
    {
      "id": 1000,
      "text": "You manage to swim across, shivering but alive. The glowing crystals ahead light your way to a massive door.",
      "type": "END"
    },
    {
      "id": 666,
      "text": "An unreachable dead end. You shouldn't be here.",
      "type": "NODE"
    }
  ]
}
$book$::jsonb),
    ('dragon-quest', NULL),
    ('pirates-jade-sea', $book$
{
  "title": "Pirates of the Jade Sea",
  "author": "Marina Blackwood",
  "difficulty": "MEDIUM",
  "type": "",
  "sections": [
    {
      "id": 1,
      "text": "The salty breeze carries the cries of distant gulls as your ship sails across the shimmering Jade Sea. Rumors speak of hidden treasure on the Isle of Serpents.",
      "type": "BEGIN",
      "options": [
        {
          "description": "Take the helm and steer toward the Isle of Serpents",
          "gotoId": 666
        },
        {
          "description": "Interrogate the captured pirate in the brig",
          "gotoId": 999
        }
      ]
    },
    {
      "id": 20,
      "text": "The captured pirate snarls but whispers about a secret cove guarded by a sea monster.",
      "type": "NODE",
      "options": [
        {
          "description": "Threaten him for more information",
          "gotoId": 200,
          "consequence": {
            "type": "LOSE_HEALTH",
            "value": "3",
            "text": "The pirate lunges at you with a hidden dagger before the guards subdue him."
          }
        },
        {
          "description": "Leave him and return to the deck",
          "gotoId": 1
        }
      ]
    },
    {
      "id": 100,
      "text": "You steer toward the mysterious Isle of Serpents. As you approach, dark storm clouds gather overhead.",
      "type": "NODE",
      "options": [
        {
          "description": "Sail through the storm",
          "gotoId": 300
        },
        {
          "description": "Drop anchor and wait for the storm to pass",
          "gotoId": 400,
          "consequence": {
            "type": "LOSE_HEALTH",
            "value": "5",
            "text": "A rogue wave crashes onto the deck, injuring several crew members."
          }
        }
      ]
    },
    {
      "id": 200,
      "text": "Shaken, the pirate reveals that the sea monster can be lulled to sleep by an enchanted conch shell hidden deep within a wrecked galleon.",
      "type": "NODE",
      "options": [
        {
          "description": "Search for the wrecked galleon",
          "gotoId": 500
        },
        {
          "description": "Ignore the legend and continue to the island",
          "gotoId": 100
        }
      ]
    },
    {
      "id": 300,
      "text": "The storm rages violently. Lightning flashes illuminate shadowy shapes beneath the waves.",
      "type": "NODE",
      "options": [
        {
          "description": "Dive into the water to investigate",
          "gotoId": 600,
          "consequence": {
            "type": "LOSE_HEALTH",
            "value": "7",
            "text": "A swirling current drags you under before the crew hauls you back aboard."
          }
        },
        {
          "description": "Stay on deck and brace for impact",
          "gotoId": 700
        }
      ]
    },
    {
      "id": 400,
      "text": "After hours of waiting, the storm clears and the Isle of Serpents looms ahead, shrouded in mist.",
      "type": "NODE",
      "options": [
        {
          "description": "Explore the island",
          "gotoId": 800
        }
      ]
    },
    {
      "id": 500,
      "text": "You find the wrecked galleon resting on a coral reef. The conch shell glows faintly within a locked chest.",
      "type": "NODE",
      "options": [
        {
          "description": "Unlock the chest carefully",
          "gotoId": 900
        },
        {
          "description": "Smash the chest open",
          "gotoId": 1000,
          "consequence": {
            "type": "LOSE_HEALTH",
            "value": "4",
            "text": "Splinters cut your hands as you force the chest open."
          }
        }
      ]
    },
    {
      "id": 600,
      "text": "Beneath the waves, you glimpse the massive serpent coiled around a treasure-laden wreck.",
      "type": "NODE",
      "options": [
        {
          "description": "Swim closer to investigate",
          "gotoId": 666
        },
        {
          "description": "Retreat back to the surface",
          "gotoId": 300
        }
      ]
    },
    {
      "id": 700,
      "text": "The storm subsides. Your ship drifts into calm waters, and the sun breaks through the clouds.",
      "type": "NODE",
      "options": [
        {
          "description": "Set a course for the Isle of Serpents",
          "gotoId": 800
        }
      ]
    },
    {
      "id": 800,
      "text": "You step onto the white sands of the Isle of Serpents. Ancient ruins rise above the jungle canopy.",
      "type": "NODE",
      "options": [
        {
          "description": "Search the ruins for treasure",
          "gotoId": 1200
        },
        {
          "description": "Climb the tallest ruin for a better view",
          "gotoId": 1300
        }
      ]
    },
    {
      "id": 900,
      "text": "Inside the chest, you find the enchanted conch shell. Its surface hums softly with power.",
      "type": "NODE",
      "options": [
        {
          "description": "Blow the conch to summon help",
          "gotoId": 1400
        },
        {
          "description": "Keep it safe for later",
          "gotoId": 800
        }
      ]
    },
    {
      "id": 1000,
      "text": "The chest bursts open, revealing the glowing conch shell.",
      "type": "NODE",
      "options": [
        {
          "description": "Take the shell",
          "gotoId": 900
        }
      ]
    },
    {
      "id": 1200,
      "text": "Inside the ruins, you discover a chamber filled with gold and jewels, but a trap triggers!",
      "type": "NODE",
      "options": [
        {
          "description": "Dodge the falling stones",
          "gotoId": 1500,
          "consequence": {
            "type": "LOSE_HEALTH",
            "value": "6",
            "text": "A rock grazes your shoulder as you narrowly escape."
          }
        },
        {
          "description": "Grab the treasure and run",
          "gotoId": 1400
        }
      ]
    },
    {
      "id": 1300,
      "text": "From the top of the ruins, you spot a hidden lagoon surrounded by jagged cliffs.",
      "type": "NODE",
      "options": [
        {
          "description": "Head toward the hidden lagoon",
          "gotoId": 1400
        }
      ]
    },
    {
      "id": 1400,
      "text": "You reach a secret pirate hideout filled with treasure. The crew celebrates your discovery.",
      "type": "END"
    },
    {
      "id": 1500,
      "text": "Breathing heavily, you escape the collapsing ruins with your life intact.",
      "type": "END"
    },
    {
      "id": 666,
      "text": "An unreachable whirlpool drags you into darkness. You shouldn’t be here.",
      "type": "NODE"
    }
  ]
}
$book$::jsonb),
    ('the-prisoner', $book$
{
  "title": "The Prisoner",
  "author": "Daniel El Fuego",
  "difficulty": "HARD",
  "sections": [
    {
      "id": 1,
      "text": "You wake up in what seems to be a dark prison cell, on an old wooden bed. Metal bars are preventing you to escape from the room. There is no window.",
      "type": "BEGIN",
      "options": [
        {
          "description": "You try to open the door",
          "gotoId": 500
        },
        {
          "description": "You look under the bed",
          "gotoId": 20
        }
      ]
    },
    {
      "id": 20,
      "text": "You don't see anything, it's too dark.",
      "type": "NODE",
      "options": [
        {
          "description": "Try to scan the area with your hands",
          "gotoId": 30,
          "consequence": {
            "type": "LOSE_HEALTH",
            "value": "6",
            "text": "As you move your hands left and right under the bed, you cut yourself on a rusty nail."
          }
        }
      ]
    },
    {
      "id": 30,
      "text": "You found what seems to be a door key.",
      "type": "NODE",
      "options": [
        {
          "description": "Try to open the door with the key",
          "gotoId": 1000
        }
      ]
    },
    {
      "id": "500",
      "text": "The door is locked.",
      "type": "NODE",
      "options": [
        {
          "description": "Gather your thoughts",
          "gotoId": 1,
          "consequence": {
            "type": "LOSE_HEALTH",
            "value": "3",
            "text": "You're getting a little more crazier..."
          }
        }
      ]
    },
    {
      "id": "1000",
      "text": "The door opens. You are now free...",
      "type": "END"
    },
    {
      "id": "666",
      "text": "Some unreachable node",
      "type": "NODE"
    }
  ]
}
$book$::jsonb);

INSERT INTO book (id, title, author, difficulty, source, section_count,
                  ending_count, valid, error_count, created_at, updated_at, version)
SELECT b.id, COALESCE(b.payload->>'title', initcap(replace(b.id, '-', ' '))),
       b.payload->>'author', b.payload->>'difficulty', 'FILE',
       jsonb_array_length(COALESCE(b.payload->'sections', '[]'::jsonb)),
       (SELECT count(*) FROM jsonb_array_elements(COALESCE(b.payload->'sections', '[]'::jsonb)) AS s(item)
        WHERE s.item->>'type' = 'END'),
       true, 0, now(), now(), 0
FROM seed_book b;

INSERT INTO book_section (book_id, section_number, position, type, text)
SELECT b.id, (s.item->>'id')::int, (s.ordinality - 1)::int,
       s.item->>'type', s.item->>'text'
FROM seed_book b
CROSS JOIN LATERAL jsonb_array_elements(b.payload->'sections') WITH ORDINALITY AS s(item, ordinality);

INSERT INTO section_option (section_id, position, description, goto_section_number,
                            consequence_type, consequence_value, consequence_text)
SELECT bs.id, (o.ordinality - 1)::int, o.item->>'description',
       (o.item->>'gotoId')::int, o.item->'consequence'->>'type',
       (o.item->'consequence'->>'value')::int, o.item->'consequence'->>'text'
FROM seed_book b
CROSS JOIN LATERAL jsonb_array_elements(b.payload->'sections') WITH ORDINALITY AS s(item, ordinality)
JOIN book_section bs ON bs.book_id = b.id AND bs.position = s.ordinality - 1
CROSS JOIN LATERAL jsonb_array_elements(COALESCE(s.item->'options', '[]'::jsonb))
    WITH ORDINALITY AS o(item, ordinality);

-- Numeric-string section ids in the-prisoner.json match numeric gotoIds after
-- normalization. Its optionless NODE is the only validation error.
INSERT INTO book_validation_error
    (book_id, position, code, message, section_number, option_index, goto_section_number)
VALUES
    ('crystal-caverns', 0, 'NODE_WITHOUT_OPTIONS', 'Section 666 is not an END section but has no options.', 666, NULL, NULL),
    ('dragon-quest', 0, 'UNPARSEABLE', 'The file is empty.', NULL, NULL, NULL),
    ('pirates-jade-sea', 0, 'INVALID_GOTO', 'Option 1 of section 1 points to section 999, which does not exist.', 1, 1, 999),
    ('pirates-jade-sea', 1, 'NODE_WITHOUT_OPTIONS', 'Section 666 is not an END section but has no options.', 666, NULL, NULL),
    ('the-prisoner', 0, 'NODE_WITHOUT_OPTIONS', 'Section 666 is not an END section but has no options.', 666, NULL, NULL);

UPDATE book b
SET error_count = (SELECT count(*) FROM book_validation_error e WHERE e.book_id = b.id),
    valid = false
WHERE b.id IN (SELECT id FROM seed_book)
  AND EXISTS (SELECT 1 FROM book_validation_error e WHERE e.book_id = b.id);
