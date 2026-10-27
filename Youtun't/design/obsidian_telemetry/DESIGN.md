---
name: Obsidian Telemetry
colors:
  surface: '#111412'
  surface-dim: '#111412'
  surface-bright: '#363a38'
  surface-container-lowest: '#0b0f0d'
  surface-container-low: '#191c1a'
  surface-container: '#1d201e'
  surface-container-high: '#272b29'
  surface-container-highest: '#323633'
  on-surface: '#e1e3e0'
  on-surface-variant: '#b9cac4'
  inverse-surface: '#e1e3e0'
  inverse-on-surface: '#2e312f'
  outline: '#83948f'
  outline-variant: '#3a4a46'
  surface-tint: '#00dfc1'
  primary: '#d7fff3'
  on-primary: '#00382f'
  primary-container: '#00f5d4'
  on-primary-container: '#006c5c'
  inverse-primary: '#006b5b'
  secondary: '#c0c8c5'
  on-secondary: '#2a3230'
  secondary-container: '#404946'
  on-secondary-container: '#aeb7b4'
  tertiary: '#e6fcf6'
  on-tertiary: '#223430'
  tertiary-container: '#cadfd9'
  on-tertiary-container: '#50635f'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#26fedc'
  primary-fixed-dim: '#00dfc1'
  on-primary-fixed: '#00201a'
  on-primary-fixed-variant: '#005144'
  secondary-fixed: '#dce4e1'
  secondary-fixed-dim: '#c0c8c5'
  on-secondary-fixed: '#151d1b'
  on-secondary-fixed-variant: '#404946'
  tertiary-fixed: '#d2e7e1'
  tertiary-fixed-dim: '#b6cbc5'
  on-tertiary-fixed: '#0c1f1b'
  on-tertiary-fixed-variant: '#384a46'
  background: '#111412'
  on-background: '#e1e3e0'
  surface-variant: '#323633'
  ice-white: '#F3F7F6'
  pine-glass: rgba(14, 22, 20, 0.72)
  pine-glass-border: rgba(0, 245, 212, 0.16)
  specular-highlight: rgba(243, 247, 246, 0.12)
  signal-crimson: '#FF3366'
  surface-glass-dim: rgba(6, 11, 9, 0.85)
typography:
  headline-xl:
    fontFamily: Space Grotesk
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 38px
    letterSpacing: -0.03em
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 30px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Space Grotesk
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 26px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Space Grotesk
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 22px
    letterSpacing: 0em
  body-lg:
    fontFamily: Inter
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 22px
    letterSpacing: -0.01em
  body-md:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 19px
    letterSpacing: -0.005em
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0em
  label-lg:
    fontFamily: JetBrains Mono
    fontSize: 13px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: -0.02em
  label-md:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 9px
    fontWeight: '600'
    lineHeight: 12px
    letterSpacing: 0.06em
  display-code:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: -0.03em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 0.75rem
  margin: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 0.75rem
  space-lg: 1.25rem
  space-xl: 1.75rem
---

## Brand & Style

This design system defines an ultra-tactile, high-precision video playback and discovery experience engineered specifically for mobile viewing. Fusing developer-grade technical UI, glassmorphic optics, and dark bento-grid modularity, the aesthetic treats video media as dynamic signal streams framed by surgical instrumentation.

The visual style merges Dark Glassmorphism with Technical Precision:
- **AMOLED Abyssal Foundation**: Surfaces merge seamlessly into edge-to-edge mobile OLED hardware, eliminating visual boundary glare.
- **Surgical Optical Glass**: Translucent pine-black surfaces with backdrop blurs and 1px directional specular highlights deliver physical depth without visual clutter.
- **Technical Telemetry**: Metadata, runtimes, stream resolutions, and metrics adopt a rigorous monospaced hierarchy, evoking pro-tier video mastering interfaces and high-density developer dashboards.

## Colors

Color is applied structurally rather than decoratively. The canvas utilizes pure abyssal obsidian (`#030504`) to preserve mobile OLED efficiency and maximize contrast against visual media.

- **Primary (`#00F5D4`)**: Used selectively for active indicators, scrubbing timelines, live telemetry badges, and primary interactive states. It provides an immediate optical anchor without washing out adjacent video content.
- **Secondary (`#0E1614`)**: Serves as the chromatic base for backdrop-filtered overlays, contextual drawer backdrops, and card containers.
- **Tertiary (`#8A9E99`)**: Carries supporting labels, timecode markers, structural dividers, and secondary actions.
- **Neutral (`#030504`)**: Anchors canvas background layers and fully occluded containers.
- **Ice White (`#F3F7F6`)**: Dedicated high-contrast text color that avoids blue-channel harshness while remaining crisp over blurred glass.
- **Signal Crimson (`#FF3366`)**: Reserved strictly for live recording tags, latency drops, or destructive actions.

## Typography

Typography establishes an intentional cadence between editorial presentation and machine readout:

- **Space Grotesk** commands top-level video headlines, tray headers, and creator titles with geometric density.
- **Inter** handles narrative scanability: comments, description copy, transcription text, and channel bios.
- **JetBrains Mono** surfaces all telemetry: video resolutions (`4K 60FPS HDR`), playback timestamps (`04:21 / 18:40`), bitrate rates, subscriber counters, and pill metadata tags. All mono typography maintains tabular figure alignment to eliminate layout shifting during live stream metric updates.

## Layout & Spacing

The layout is built for high-density mobile ergonomics with a modular Bento grid structure:

- **Mobile Viewport Focus**: The layout operates across a 4-column base grid (16px outer margins, 12px gutters) expanding to an 8-column adaptive grid on foldables and tablets.
- **Thumb-Zone Mechanics**: Primary scrubber controls, channel triggers, and primary actions are pinned within the lower 40% vertical zone of the viewport. Secondary technical telemetry docks into upper-third floating glass clusters.
- **Bento Tile Modular Architecture**: Feed cards, comment summaries, chapter overviews, and live stream telemetry exist as distinct bento pods separated by consistent `space-sm` or `space-md` gaps.

## Elevation & Depth

Visual hierarchy does not use diffuse drop shadows. Depth is achieved entirely through optical translucency, layered alpha borders, and specular edge lighting:

- **Base Layer (Level 0)**: Obsidian `#030504`, opaque, noise-dithered floor.
- **Bento Layer (Level 1)**: Translucent pine glass (`rgba(14, 22, 20, 0.72)`) with a `backdrop-filter: blur(20px)` and an outer 1px structural stroke of `rgba(243, 247, 246, 0.08)`.
- **Active / Interactive Layer (Level 2)**: Elevated glass pods and active controls utilize `rgba(14, 22, 20, 0.88)` with a top-weighted specular highlight (`border-top: 1px solid rgba(243, 247, 246, 0.22)`) paired with an interior mint luminescence (`box-shadow: inset 0 1px 0 rgba(0, 245, 212, 0.2)`).
- **HUD & Modal Drawers (Level 3)**: Pinned video telemetry and sheet trays feature `rgba(6, 11, 9, 0.92)` glass, `backdrop-filter: blur(32px)`, and an encompassing hairline boundary (`rgba(0, 245, 212, 0.24)`).

## Shapes

The interface balances precision-cut technical discipline with finger-friendly curvature:

- **Interactive Cards & Bento Pods**: Styled with `rounded-lg` (16px radius) to maintain structural containment.
- **Telemetry Chips & Action Controls**: Rendered as pills or compact squircle tabs with `rounded-lg` (8px to 12px internal radius).
- **Micro Badges**: Badges and status pills leverage micro-fillets (4px) to retain an instrumental diagnostic identity.

## Components

### Buttons & Scrubbing Controls
- **Primary Action (e.g., Subscribe / Join)**: High-luminance mint surface (`#00F5D4`) with pitch-black (`#030504`) typography in Space Grotesk medium. Crisp, unblurred 1px outer rim with no drop shadow.
- **Ghost Utility Action**: Pine glass backdrop (`rgba(14, 22, 20, 0.72)`), 1px specular top edge, ice white icons with slate-mint hover transition.
- **Scrubber Bar**: Unbuffered timeline in `rgba(243, 247, 246, 0.15)`, loaded buffer in `rgba(138, 158, 153, 0.4)`, and active playback head illuminated in `#00F5D4` accompanied by a pulsing 2px glow ring.

### Telemetry Pills & Chips
- **Stream Diagnostic Chips**: Height 22px, `JetBrains Mono` label-sm text. Background is `rgba(0, 245, 212, 0.06)`, stroke is `rgba(0, 245, 212, 0.25)`, text is `#00F5D4`. Displays resolution, audio codec, and FPS.
- **Filter Tags**: Translucent dark pine background, displaying count index in mono prefix (`[01] Trending`, `[02] Coding`).

### Bento Feed Cards
- Composed of 16:9 media modules paired with a bottom technical HUD.
- The thumbnail container features an inline duration badge pinned to the bottom right with `label-sm` monospaced digits over `rgba(3, 5, 4, 0.85)` glass.
- Channel avatar is framed with a 1px border (`rgba(0, 245, 212, 0.4)`), flanked by a two-line text hierarchy: Space Grotesk video title followed by slate-mint channel attribution and view metrics.

### Inputs & Live Chat
- **Chat Input Pod**: Floating bottom glass capsule with `backdrop-filter: blur(24px)`.
- **Field**: Inset background (`rgba(3, 5, 4, 0.6)`), 1px border (`rgba(243, 247, 246, 0.1)`), focusing into a surgical mint highlight (`rgba(0, 245, 212, 0.6)`).
- **Live Stream Comments**: Monospace user handles (`JetBrains Mono` label-md) colored dynamically within a cool-hue spectrum, with message strings in `Inter` body-md for readability.

### Sliders, Toggles & Checkboxes
- **Toggle**: Mechanical track (`rgba(14, 22, 20, 0.9)`) with a 1px border. Thumb snaps into `#00F5D4` when active, emitting an inner specular gleam.
- **Checkbox / Radio**: Sharp-cornered squircles (4px) with high-contrast mint crosshairs or dot centers.