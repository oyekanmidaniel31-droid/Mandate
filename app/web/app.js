// Mandate: The Long Game - PWA Game Engine

const lifeStages = [
  { id: 'CAMPUS', name: 'CAMPUS POLITICS' },
  { id: 'HUSTLE_YEARS', name: 'HUSTLE & NYSC' },
  { id: 'STATE_POLITICS', name: 'STATE HOUSE & COMMISSIONER' },
  { id: 'NATIONAL_ASSEMBLY', name: 'NATIONAL ASSEMBLY' },
  { id: 'PRESIDENTIAL_BID', name: 'THE PRESIDENCY' }
];

let gameState = {
  playerName: "Chief Adebayo",
  stageIndex: 0,
  eventIndex: 0,
  nara: 250000,
  influence: 20,
  familyLoyalty: 80,
  energy: 100,
  health: 90,
  stress: 15,
  ambition: 50,
  conscience: 50,
  politicalCapital: 10,
  godfatherFavor: 30,
  angerIndex: 5,
  riskAppetite: 30
};

const gameEvents = {
  CAMPUS: [
    {
      category: "Campus Admission Drama",
      headline: "The JAMB Cut-Off Switcheroo",
      narration: "You applied to study Medicine at the premier Federal University of Lakos with a score of 285. But when the merit list comes out on the departmental notice board, your name appears under 'Fisheries & Agricultural Science'.",
      pidginQuote: "Head of Admin whisper: 'My pikin, admission na by connection. If your papa no be general, na catfish you go rear.'",
      options: [
        {
          label: "Pay ₦150,000 'Registrar Sorting Fee' to switch to Law",
          description: "Borrow money from your elder cousin and grease the registrar's palms.",
          naraDelta: -150000, energyDelta: -20, influenceDelta: 10, ambitionDelta: 15, conscienceDelta: -10, polCapDelta: 15,
          outcome: "Within 48 hours, your admission slip magically transformed into Faculty of Law. Welcome to legal loopholes."
        },
        {
          label: "Embrace Agriculture & become student union farmer",
          description: "Study the course proudly, sell organic eggs to female hostels, and build grassroot popularity.",
          naraDelta: 20000, energyDelta: -15, influenceDelta: 20, ambitionDelta: 5, conscienceDelta: 15, stressDelta: -10,
          outcome: "Your student farm business boomed! Hostel students nickname you 'The Agric Messiah'. You now control the student vote."
        },
        {
          label: "Organize a student sit-in protest against quota injustice",
          description: "Mobilize rejected candidates with placards in front of the Senate Building.",
          naraDelta: -15000, energyDelta: -25, influenceDelta: 35, ambitionDelta: 20, stressDelta: 20, angerIndexDelta: 5,
          outcome: "Security fired water cannons, but TV stations broadcast your fiery speech. You are suddenly a campus celebrity."
        }
      ]
    },
    {
      category: "Hostel Turf War",
      headline: "Midnight Knock: The Secret Brotherhood",
      narration: "At 2:00 AM, heavy footsteps echo outside your hostel room. Three masked senior students wearing black berets step in. They invite you to join the 'Pyramid Vanguard'—the most influential campus brotherhood.",
      pidginQuote: "Leader hiss: 'You get mouth for campus. Either you join the table or you become the suya on it.'",
      options: [
        {
          label: "Take the blood oath and join the brotherhood",
          description: "Gain violent protection and lifelong fraternity connections in high places.",
          naraDelta: -50000, energyDelta: -20, influenceDelta: 25, ambitionDelta: 25, conscienceDelta: -25, godfatherDelta: 15, stressDelta: 20,
          outcome: "You endured the brutal initiation. Now when you walk through campus, even security guards bow in deference."
        },
        {
          label: "Decline politely and join the University Debate Clinic",
          description: "Keep your hands clean and build reputation purely through intellectual grit.",
          naraDelta: -10000, energyDelta: -15, influenceDelta: 15, ambitionDelta: 10, conscienceDelta: 20, healthDelta: 5,
          outcome: "They whispered threats, but you won the All-Nigeria Inter-Varsity Debate. The Vice-Chancellor shook your hand."
        },
        {
          label: "Run for SUG President as an independent candidate",
          description: "Channel student frustration against brotherhood intimidation into a mass electoral campaign.",
          naraDelta: -80000, energyDelta: -30, influenceDelta: 40, ambitionDelta: 30, conscienceDelta: 10, polCapDelta: 25, stressDelta: 25,
          outcome: "You swept all five faculties in a landslide! You are now SUG President, commanding 22,000 students."
        }
      ]
    }
  ],
  HUSTLE_YEARS: [
    {
      category: "Youth Service (NYSC)",
      headline: "The Remote Northern Posting",
      narration: "Your National Youth Service call-up letter drops: you have been posted to a remote desert LGA bordering Lake Chad, 1,400km away from your networks.",
      pidginQuote: "NYSC official at camp gate: 'If you want relocation to ministry in Abuka, bring ₦250k or go face the desert.'",
      options: [
        {
          label: "Pay ₦250,000 to redeploy to the Ministry of Finance in Abuka",
          description: "Position yourself right beside federal contract directors.",
          naraDelta: -250000, energyDelta: -15, influenceDelta: 15, ambitionDelta: 20, conscienceDelta: -10, polCapDelta: 20, godfatherDelta: 20,
          outcome: "You landed in Abuka! In corridors of power, you photocopied confidential budget memos and shook hands with senators."
        },
        {
          label: "Accept remote posting and teach rural children",
          description: "Build grassroot connection with local emirs and nomadic communities.",
          naraDelta: 0, energyDelta: -25, influenceDelta: 15, ambitionDelta: 5, conscienceDelta: 25, familyDelta: 10, stressDelta: -5,
          outcome: "The villagers loved your selfless dedication. The local Emir awarded you an honorary youth chieftaincy: 'Dan Haske'."
        },
        {
          label: "Ghost camp and run a Crypto & POS syndicate in Lakos",
          description: "Bribe the NYSC inspector ₦30k monthly while trading volatile coins and agency banking.",
          naraDelta: 850000, energyDelta: -20, influenceDelta: 20, ambitionDelta: 25, riskDelta: 20, conscienceDelta: -15,
          outcome: "Your POS chain pulled ₦850k pure profit monthly during currency scarcity! You bought your first foreign-used Lexus."
        }
      ]
    }
  ],
  STATE_POLITICS: [
    {
      category: "Godfather Bargain",
      headline: "The Kingmaker's Palm-Greasing",
      narration: "Chief Alao, the dreaded political godfather of the Southwest zone, summons you to his GRA mansion. He wants to back your run for State Commissioner, but with strings attached.",
      pidginQuote: "Chief Alao puff cigar: 'I go give you ticket and structure. But 30% of monthly ministry contracts go flow back to my foundation. Abi you wan chop alone?'",
      options: [
        {
          label: "Sign the Godfather pact and accept his formidable backing",
          description: "Secure guaranteed victory at the polls in exchange for financial kickbacks.",
          naraDelta: 2000000, energyDelta: -10, influenceDelta: 30, godfatherDelta: 40, conscienceDelta: -30, polCapDelta: 30,
          outcome: "You won in a landslide with thugs clearing opposition polling booths. You are now Honourable Commissioner!"
        },
        {
          label: "Refuse the pact and run a grassroots populist campaign",
          description: "Appeal directly to market women and okada riders against godfathers.",
          naraDelta: -500000, energyDelta: -35, influenceDelta: 35, godfatherDelta: -30, conscienceDelta: 30, familyDelta: -15,
          outcome: "Thugs smashed your sound system at rallies, but social media went wild with your 'Freedom' mantra. You barely squeaked through!"
        }
      ]
    }
  ],
  NATIONAL_ASSEMBLY: [
    {
      category: "Legislative Oversight",
      headline: "The Multi-Billion Power Sector Probe",
      narration: "As Chairman of the House Committee on Power, your committee uncovers ₦40 billion missing from the rural electrification fund. The Managing Director of the agency visits your Abuja hotel suite at midnight with a Ghana-Must-Go bag.",
      pidginQuote: "MD whisper: 'Distinguished Chairman, make we settle this matter for table. This bag contain $150k cash. Close eye for committee report.'",
      options: [
        {
          label: "Accept the Ghana-Must-Go bag and bury the investigation",
          description: "Stash foreign currency in your Abuja safe and issue a clean bill of health.",
          naraDelta: 15000000, energyDelta: -5, influenceDelta: 10, conscienceDelta: -40, riskDelta: 30,
          outcome: "The funds were cleared. You bought a luxury mansion in Banana Island, but whistleblower blogs whisper your name."
        },
        {
          label: "Broadcast the full probe live on national television",
          description: "Expose the corruption rings and champion reform on the floor of the House.",
          naraDelta: -1000000, energyDelta: -30, influenceDelta: 50, conscienceDelta: 40, polCapDelta: 40, angerIndexDelta: 25,
          outcome: "You became a national hero overnight! Anti-corruption civil societies praise your courage, but corrupt cabals plot your downfall."
        }
      ]
    }
  ],
  PRESIDENTIAL_BID: [
    {
      category: "The Ultimate Ballot",
      headline: "National Elections: The Mandate Declared",
      narration: "The presidential election results are being tallied at the National Collation Centre in Abuja. You are running neck-and-neck with the ruling party incumbent. Tensions across Naijaland are boiling over.",
      pidginQuote: "Campaign DG sweating: 'Sir, the electoral chairman is under pressure from military generals to truncate results in three states. We need to deploy all our political capital.'",
      options: [
        {
          label: "Deploy massive coalition pressure and peaceful mass rallies",
          description: "Mobilize international observers, youth social media armies, and religious leaders.",
          naraDelta: -20000000, energyDelta: -40, influenceDelta: 60, polCapDelta: 50, conscienceDelta: 30,
          outcome: "The international community pressured the electoral commission to release authentic results. You have won the Mandate!"
        },
        {
          label: "Engage underground backroom compromises with military kingmakers",
          description: "Promise key cabinet posts and oil blocks to generals to secure their proclamation.",
          naraDelta: -10000000, energyDelta: -20, influenceDelta: 40, godfatherDelta: 50, conscienceDelta: -50,
          outcome: "The generals stepped in and broadcasted your swearing-in as President of the Federal Republic of Naijaland. You hold ultimate power now."
        }
      ]
    }
  ]
};

function initGame() {
  const saved = localStorage.getItem('mandate_save');
  if (saved) {
    try {
      gameState = JSON.parse(saved);
    } catch(e) {}
  }
  renderUI();
}

function saveGame() {
  localStorage.setItem('mandate_save', JSON.stringify(gameState));
}

function renderUI() {
  document.getElementById('hudNara').innerText = '₦' + gameState.nara.toLocaleString();
  document.getElementById('hudInfluence').innerText = gameState.influence + '%';
  document.getElementById('hudLoyalty').innerText = gameState.familyLoyalty + '%';
  document.getElementById('hudEnergy').innerText = gameState.energy;
  document.getElementById('hudHealth').innerText = gameState.health + '%';
  document.getElementById('hudStress').innerText = gameState.stress + '%';

  const currentStageObj = lifeStages[gameState.stageIndex];
  document.getElementById('stageBadge').innerText = currentStageObj.name;
  document.getElementById('playerNameDisplay').innerText = gameState.playerName;

  const eventsList = gameEvents[currentStageObj.id];
  const currentEvent = eventsList[gameState.eventIndex % eventsList.length];

  document.getElementById('eventCategory').innerText = currentEvent.category;
  document.getElementById('eventHeadline').innerText = currentEvent.headline;
  document.getElementById('eventNarration').innerText = currentEvent.narration;
  document.getElementById('pidginQuote').innerText = currentEvent.pidginQuote;

  const choicesContainer = document.getElementById('choicesList');
  choicesContainer.innerHTML = '';

  currentEvent.options.forEach((opt, idx) => {
    const btn = document.createElement('button');
    btn.className = 'choice-btn';
    btn.innerHTML = `
      <div class="choice-label">${idx + 1}. ${opt.label}</div>
      <div class="choice-desc">${opt.description}</div>
    `;
    btn.onclick = () => makeChoice(opt);
    choicesContainer.appendChild(btn);
  });
}

function makeChoice(opt) {
  gameState.nara += (opt.naraDelta || 0);
  gameState.influence = Math.min(100, Math.max(0, gameState.influence + (opt.influenceDelta || 0)));
  gameState.familyLoyalty = Math.min(100, Math.max(0, gameState.familyLoyalty + (opt.familyDelta || 0)));
  gameState.energy = Math.max(0, gameState.energy + (opt.energyDelta || -10));
  gameState.health = Math.min(100, Math.max(0, gameState.health + (opt.healthDelta || 0)));
  gameState.stress = Math.min(100, Math.max(0, gameState.stress + (opt.stressDelta || 5)));
  gameState.ambition = Math.min(100, Math.max(0, gameState.ambition + (opt.ambitionDelta || 0)));
  gameState.conscience = Math.min(100, Math.max(0, gameState.conscience + (opt.conscienceDelta || 0)));
  gameState.politicalCapital += (opt.polCapDelta || 0);
  gameState.godfatherFavor += (opt.godfatherDelta || 0);
  gameState.angerIndex += (opt.angerIndexDelta || 0);
  gameState.riskAppetite += (opt.riskDelta || 0);

  showOutcomeModal(opt.outcome, opt);
}

function showOutcomeModal(outcomeText, opt) {
  document.getElementById('outcomeTitle').innerText = "Decision Outcome";
  document.getElementById('outcomeText').innerText = outcomeText;
  
  let deltas = [];
  if (opt.naraDelta) deltas.push(`Nara: ${opt.naraDelta > 0 ? '+' : ''}₦${opt.naraDelta.toLocaleString()}`);
  if (opt.influenceDelta) deltas.push(`Influence: ${opt.influenceDelta > 0 ? '+' : ''}${opt.influenceDelta}%`);
  if (opt.polCapDelta) deltas.push(`Political Capital: +${opt.polCapDelta}`);
  if (opt.conscienceDelta) deltas.push(`Conscience: ${opt.conscienceDelta > 0 ? '+' : ''}${opt.conscienceDelta}`);
  
  document.getElementById('outcomeDeltas').innerText = deltas.join(' | ') || 'Status updated.';
  document.getElementById('outcomeModal').classList.add('active');
}

function proceedNextEvent() {
  document.getElementById('outcomeModal').classList.remove('active');
  gameState.eventIndex++;
  
  const currentStageObj = lifeStages[gameState.stageIndex];
  const eventsList = gameEvents[currentStageObj.id];
  
  if (gameState.eventIndex >= eventsList.length) {
    gameState.stageIndex++;
    gameState.eventIndex = 0;
    if (gameState.stageIndex >= lifeStages.length) {
      alert("Congratulations! You have conquered Naijaland politics and secured the Ultimate Mandate!");
      gameState.stageIndex = lifeStages.length - 1;
    } else {
      alert("Advancing to next political life stage: " + lifeStages[gameState.stageIndex].name);
    }
  }
  
  saveGame();
  renderUI();
}

// Tooltips
function showTooltip(id) {
  document.getElementById(id).classList.add('show');
}
function hideTooltip(id) {
  document.getElementById(id).classList.remove('show');
}
function toggleTooltip(id) {
  const el = document.getElementById(id);
  el.classList.toggle('show');
}

// Modals
function openStatsModal() {
  document.getElementById('dossierSummary').innerHTML = `
    <strong>Aspirant:</strong> ${gameState.playerName}<br>
    <strong>Current Stage:</strong> ${lifeStages[gameState.stageIndex].name}<br>
    <strong>Net Worth:</strong> ₦${gameState.nara.toLocaleString()}<br>
    <strong>Political Standing:</strong> Active Naijaland Contender
  `;
  document.getElementById('statAmbition').innerText = gameState.ambition;
  document.getElementById('statConscience').innerText = gameState.conscience;
  document.getElementById('statPolCap').innerText = gameState.politicalCapital;
  document.getElementById('statGodfather').innerText = gameState.godfatherFavor;
  document.getElementById('statAnger').innerText = gameState.angerIndex;
  document.getElementById('statRisk').innerText = gameState.riskAppetite;
  
  document.getElementById('statsModal').classList.add('active');
}
function closeStatsModal() {
  document.getElementById('statsModal').classList.remove('active');
}

function openPhoneModal() {
  const feed = document.getElementById('phoneFeed');
  feed.innerHTML = `
    <div style="background: rgba(56,189,248,0.1); padding: 10px; border-radius: 8px; border-left: 3px solid #38bdf8;">
      <strong>Padi Tari:</strong> "Chief, make sure you wire the youth caucus before tomorrow rally o! Dem dey vex."
    </div>
    <div style="background: rgba(244,178,81,0.1); padding: 10px; border-radius: 8px; border-left: 3px solid #f4b251;">
      <strong>Naija Globe Breaking:</strong> "Anti-Corruption Agency invites former commissioner over uncounted transit funds."
    </div>
    <div style="background: rgba(52,211,153,0.1); padding: 10px; border-radius: 8px; border-left: 3px solid #34d399;">
      <strong>Spouse:</strong> "Please remember say school fees for the twins due next week. No use all money play politics."
    </div>
  `;
  document.getElementById('phoneModal').classList.add('active');
}
function closePhoneModal() {
  document.getElementById('phoneModal').classList.remove('active');
}

function triggerRestartModal() {
  if (confirm("Are you sure you want to restart your political journey from Campus?")) {
    localStorage.removeItem('mandate_save');
    gameState = {
      playerName: "Chief Adebayo",
      stageIndex: 0,
      eventIndex: 0,
      nara: 250000,
      influence: 20,
      familyLoyalty: 80,
      energy: 100,
      health: 90,
      stress: 15,
      ambition: 50,
      conscience: 50,
      politicalCapital: 10,
      godfatherFavor: 30,
      angerIndex: 5,
      riskAppetite: 30
    };
    renderUI();
  }
}

function switchTab(tab) {
  // Navigation tabs handler
}

window.onload = initGame;
