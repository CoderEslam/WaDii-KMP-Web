# WaDii — Color Identity

Source of truth: `composeApp/src/commonMain/kotlin/com/chaaraapp/wadii/theme/Color.kt` and `theme/SpaceTheme.kt`.
This document exists so the **web frontend** can reproduce the exact same brand/theme colors as the Compose Multiplatform app.

The app ships two themes: **Dark ("Space")** and **Light ("Pearl Cosmos")**. Both share the same brand accent (`Yellow`).

---

## 1. Brand

| Name  | Hex       | Notes                              |
|-------|-----------|-------------------------------------|
| Yellow | `#FFD300` | Primary brand accent — "Star gold" |
| Red    | `#BB0000` | Brand red                          |
| Hint   | `#6B7E99` | Dim starlight (muted text)         |

---

## 2. Dark Theme — "Space"

### Backgrounds

| Name         | Hex       | Notes                     |
|--------------|-----------|---------------------------|
| SpaceVoid    | `#000814` | Deepest background        |
| SpaceDeep    | `#020C1B` | Deep space                |
| SpaceMid     | `#061224` | Mid space layer           |
| SpaceSurface | `#0A1930` | Surface / card base       |
| SpaceCard    | `#0D1F3C` | Elevated card             |

### Text

| Name        | Hex       | Notes                    |
|-------------|-----------|---------------------------|
| StarWhite   | `#E8F0FE` | Primary text (near-white) |
| StarSilver  | `#C8D6E8` | Secondary text            |
| HintColor   | `#6B7E99` | Dimmed / placeholder text |

### Cosmic Accents

| Name        | Hex       |
|-------------|-----------|
| NebulaBlue  | `#1E40AF` |
| NebulaPurple| `#7C3AED` |
| CosmicTeal  | `#0891B2` |
| AuroraCyan  | `#06B6D4` |

### Glassmorphism (overlay on dark backgrounds)

| Name          | RGBA (hex+alpha) | Notes                        |
|---------------|-------------------|-------------------------------|
| GlassWhite    | `#FFFFFF1A` (10%) | Glass fill                    |
| GlassBorder   | `#FFFFFF33` (20%) | Glass border                  |
| GlassHighlight| `#FFFFFF40` (25%) | Glass top-edge highlight      |

### Navigation (floating glass bar — always dark, even in light theme)

| Name           | Hex / RGBA        | Notes                          |
|----------------|--------------------|----------------------------------|
| NavBackground  | `#050E1D`          | Nav bar background               |
| ActiveBg       | `#FFD300` (15%)    | Active tab pill background       |
| ActiveIcon     | `#000814`          | Icon color on the active (yellow) pill |
| ActiveLabel    | `#FFD300`          | Active tab label                 |
| ActiveDot      | `#FFD300`          | Active indicator dot              |
| InactiveIcon   | `#FFFFFF7F` (50%)  | Inactive icon                     |
| InactiveLabel  | `#FFFFFF7F` (50%)  | Inactive label                    |
| DividerColor   | `#1A2D45`          | Nav divider                       |

### Semantic / Status (badges, chips, alerts)

| Purpose | Background | Foreground |
|---------|------------|------------|
| Purple  | `#7C3AED` (15%) — `PurpleBg`  | `#B39DFF` — `PurpleFg` |
| Teal    | `#0891B2` (15%) — `TealBg`    | `#67E8F9` — `TealFg`   |
| Blue    | `#1E40AF` (15%) — `BlueBg`    | `#93C5FD` — `BlueFg`   |
| Amber   | `#B45309` (15%) — `AmberBg`   | `#FCD34D` — `AmberFg`  |
| Green   | `#065F46` (15%) — `GreenBg`   | `#6EE7B7` — `GreenFg`  |
| Gray    | `#FFFFFF` (10%) — `GrayBg`    | `#94A3B8` — `GrayFg`   |
| Red     | `#991B1B` (15%) — `RedBg`     | `#FCA5A5` — `RedFg`    |
| Yellow  | `#FFD300` (15%) — `YellowAlpha` | `#FFD300` — `Yellow` |

### Misc

| Name        | Hex       | Notes         |
|-------------|-----------|----------------|
| SurfaceBg   | `#0D1F3C` | Same as SpaceCard |
| BorderColor | `#1A2D45` | Default border |
| DividerSoft | `#0F1E35` | Soft divider   |

### Header gradient (dark)
`#0F1E38 → #1A1040 → #0C1E3A`

---

## 3. Light Theme — "Pearl Cosmos"

| Name          | Hex       | Notes                          |
|---------------|-----------|----------------------------------|
| LightBg       | `#F5EEFF` | Pearl lavender root background   |
| LightSurface  | `#EDE6FF` | Soft violet surface               |
| LightCard     | `#F8F4FF` | Near-white card                  |
| LightText     | `#0E0527` | Deep cosmic indigo (primary text)|
| LightTextDim  | `#7060A0` | Muted nebula purple (secondary text) |
| Accent        | `#FFD300` | Same brand Yellow                 |
| Icon tint     | `#0E0527` | Icon color                        |
| Divider       | `#0E0527` (8%)  | |
| Search bg     | `#0E0527` (6%)  | |
| Search border | `#0E0527` (14%) | |

### Header gradient (light)
`#CFBEFF → #BFA8FF → #CDBEFF`

> Note: the navigation bar stays on the **dark** palette even when the light theme is active (`NavBackground #050E1D`, white-based unselected icon/label).

---

## 4. Material Color Scheme Mapping

| Role              | Dark        | Light       |
|-------------------|-------------|-------------|
| primary           | `#FFD300`   | `#FFD300`   |
| onPrimary         | `#000814`   | `#000814`   |
| secondary         | `#7C3AED`   | `#7C3AED`   |
| onSecondary       | `#E8F0FE`   | `#FFFFFF`   |
| background        | `#000814`   | `#F5EEFF`   |
| onBackground      | `#E8F0FE`   | `#0E0527`   |
| surface           | `#0D1F3C`   | `#F8F4FF`   |
| onSurface         | `#E8F0FE`   | `#0E0527`   |
| surfaceVariant    | `#0A1930`   | `#EDE6FF`   |
| onSurfaceVariant  | `#E8F0FE`   | `#7060A0`   |
| outline           | `#1A2D45`   | `#CBBFEE`   |
| error             | `#FCA5A5`   | `#D32F2F`   |
| onError           | `#000814`   | `#FFFFFF`   |

---

## 5. CSS Custom Properties (drop-in for web)

```css
:root {
  /* Brand */
  --color-yellow: #FFD300;
  --color-red: #BB0000;
  --color-hint: #6B7E99;
}

/* Dark theme ("Space") */
[data-theme="dark"] {
  --bg: #000814;
  --bg-deep: #020C1B;
  --bg-mid: #061224;
  --surface: #0A1930;
  --card: #0D1F3C;

  --text: #E8F0FE;
  --text-secondary: #C8D6E8;
  --text-dim: #6B7E99;

  --accent: var(--color-yellow);
  --nav-bg: #050E1D;
  --nav-active-bg: rgba(255, 211, 0, 0.15);
  --nav-active-icon: #000814;
  --nav-active-label: var(--color-yellow);
  --nav-inactive: rgba(255, 255, 255, 0.5);
  --divider: #1A2D45;
  --divider-soft: #0F1E35;

  --glass-fill: rgba(255, 255, 255, 0.10);
  --glass-border: rgba(255, 255, 255, 0.20);
  --glass-highlight: rgba(255, 255, 255, 0.25);

  --nebula-blue: #1E40AF;
  --nebula-purple: #7C3AED;
  --cosmic-teal: #0891B2;
  --aurora-cyan: #06B6D4;

  --header-gradient: linear-gradient(135deg, #0F1E38, #1A1040, #0C1E3A);
}

/* Light theme ("Pearl Cosmos") */
[data-theme="light"] {
  --bg: #F5EEFF;
  --surface: #EDE6FF;
  --card: #F8F4FF;

  --text: #0E0527;
  --text-dim: #7060A0;

  --accent: var(--color-yellow);
  --divider: rgba(14, 5, 39, 0.08);
  --search-bg: rgba(14, 5, 39, 0.06);
  --search-border: rgba(14, 5, 39, 0.14);
  --outline: #CBBFEE;
  --error: #D32F2F;

  --header-gradient: linear-gradient(135deg, #CFBEFF, #BFA8FF, #CDBEFF);

  /* Nav bar always uses the dark palette */
  --nav-bg: #050E1D;
  --nav-inactive: rgba(255, 255, 255, 0.5);
}
```
