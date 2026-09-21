ULTIMATE AI KEYBOARD — COMPLETE MERGED MASTER PROMPT
=============================================================

IMPORTANT:
This document combines the original master prompt, the every-angle engineering audit,
and the final micro-polish audit into ONE prompt. Use this as the primary master
instruction for Claude when working on the existing AI keyboard project.

ULTIMATE AI KEYBOARD — MASTER PRODUCTION COMPLETION PROMPT

You are working on my EXISTING Android AI keyboard project.

Your job is NOT to blindly rebuild it. Your job is to inspect what already exists, understand it, preserve what works, improve what needs improvement, implement what is missing, and take the entire project toward a polished production-quality Android application.

============================================================
PHASE 0 — MANDATORY: READ THE EXISTING PROJECT FIRST
============================================================

THIS IS THE MOST IMPORTANT RULE.

BEFORE YOU MODIFY, DELETE, REPLACE, MOVE, OR GENERATE ANY PROJECT FILE:

STOP AND INSPECT THE EXISTING PROJECT.

You MUST first:
1. Read the existing project structure.
2. Read all relevant source files.
3. Read existing keyboard/IME code.
4. Read existing UI code.
5. Read existing settings code.
6. Read existing AI code.
7. Read existing prediction/autocorrect code.
8. Read existing clipboard code.
9. Read existing storage/database code.
10. Read networking/API code.
11. Read AndroidManifest.
12. Read Gradle/build configuration.
13. Read dependency configuration.
14. Read relevant resources.
15. Read existing tests.
16. Inspect themes/styles.
17. Inspect navigation.
18. Inspect permissions.
19. Inspect release/debug configuration.
20. Identify existing integrations.

Build a clear understanding of how the current application works BEFORE changing anything.

DO NOT assume a feature is missing.
DO NOT assume a file is incomplete.
DO NOT overwrite an old file simply because you prefer another implementation.
DO NOT create duplicate systems.
DO NOT rebuild working functionality without a genuine technical reason.

The EXISTING PROJECT is the source of truth.

For every improvement:
1. Find the existing implementation.
2. Read it.
3. Understand dependencies.
4. Determine whether it already works.
5. Determine what actually needs changing.
6. Modify the existing implementation where appropriate.
7. Preserve existing behavior.
8. Test the change.

If a file already contains functionality related to the task, READ THAT FILE BEFORE TOUCHING IT.

============================================================
PHASE 1 — COMPLETE PROJECT RECONNAISSANCE
============================================================

Map the architecture and identify:
- Android framework/language/UI framework
- Architecture pattern
- Keyboard/IME architecture
- AI architecture
- Networking
- Local storage/database
- Settings/preferences
- Prediction engine
- Autocorrect
- Clipboard
- Emoji/symbol systems
- Theme system
- Language system
- Navigation
- Permissions
- Dependencies
- Tests
- Build/release configuration

Identify:
- Fully implemented features
- Partially implemented features
- Broken features
- Placeholder/fake features
- Dead/duplicate code
- Bugs
- Missing production infrastructure
- Security concerns
- Performance concerns

Do not change architecture merely for personal preference.

============================================================
PHASE 2 — MICRO KEYBOARD DETAILS
============================================================

Audit and improve where appropriate:
- Key press feedback
- Key pop-up preview
- Key repeat
- Backspace repeat
- Long press
- Long-press alternatives
- Shift
- Caps Lock
- Auto-capitalization
- Spacebar
- Double-space punctuation
- Comma
- Period
- Apostrophe
- Quotes
- Brackets
- Cursor
- Selection
- Copy/Cut/Paste
- Undo/Redo
- Select all
- Cursor movement
- Gesture behavior
- Swipe typing
- Number layout
- Symbol layout
- Emoji layout
- Language switching
- Keyboard switching
- Toolbar
- Suggestion bar

Do not let improvements interfere with normal typing.

============================================================
PHASE 3 — LONG-PRESS SHORTCUTS
============================================================

SPECIFIC REQUIREMENT:

Long-pressing the comma key must open Keyboard Settings.

Normal comma tap:
→ inserts ","

Long press:
→ opens Keyboard Settings.

Make it reliable and prevent accidental punctuation insertion.

Audit other keys for sensible long-press alternatives.

============================================================
PHASE 4 — SYMBOLS AND CURRENCY
============================================================

Provide comprehensive symbols, including where appropriate:

$
€
£
¥
₩
₹
₽
₺
₦
₵
₡
₫
₱
₲
₴
₸
₼
฿
₿

Also:
+ − × ÷ = ≠ ≈ ≤ ≥ % ‰ √ ∞ ° ^ ~ < > | \ _ • © ® ™ § ¶ & @ # * ( ) [ ] { } : ; / ` " '

Add other useful characters, sensible long-press alternatives, and logical organization.

============================================================
PHASE 5 — KEYBOARD TOOLBAR / UTILITY PANEL
============================================================

Create/refine a polished expandable utility area.

Possible tools:
- Clipboard
- Emoji
- GIF
- Symbols
- Translate
- AI
- Rewrite
- Grammar
- Search
- Voice typing
- One-handed mode
- Floating keyboard
- Resize
- Themes
- Settings
- Text editing
- More tools

Support:
- Add
- Remove
- Reorder
- Pin
- Favorites
- Recently used
- Hide
- Restore defaults

Remember user customization.

Never display non-functional tools.

============================================================
PHASE 6 — WORKSPACE BEHAVIOR
============================================================

IMPORTANT UI RULE:

Tools such as Emoji and Tone should use the keyboard's KEY AREA rather than appearing as a large extra panel above the keyboard.

Normal mode:
[toolbar/suggestions]
[key rows]
[bottom controls]

Emoji mode:
[toolbar/suggestions]
[emoji grid occupying the key area]
[bottom controls]

Tone mode:
[toolbar/suggestions]
[tone choices/workspace occupying the key area]
[bottom controls]

Do NOT create a second keyboard underneath the workspace.
Do NOT push the workspace above the entire keyboard.
Do NOT create an unrelated separate screen when an integrated keyboard workspace is appropriate.

Provide an obvious ABC/keyboard/back control to return to normal letter keys.

The same integrated workspace principle should be used for GIF, Symbols, Clipboard, AI tools, Translate, etc. where technically appropriate.

============================================================
PHASE 7 — TONE BEHAVIOR
============================================================

Tone must behave as a real keyboard mode/workspace.

Examples:
- Casual
- Formal
- Funny
- Professional
- Friendly
- Flirty
- Confident
- Polite
- Short
- Creative
- Serious
- Custom

When a tone is selected:
- Keep it active while the user works with the Tone feature.
- Do not make it disappear after one action.
- Allow another tone to replace it.
- Allow the user to return to the normal keyboard.
- Do not randomly close the workspace.

Preserve the selected tone appropriately until the user changes/exits it.

============================================================
PHASE 8 — INPUT SESSION / WORKSPACE RESET
============================================================

Temporary workspace state must belong to the CURRENT editing context.

Problem to prevent:
Chat A → Emoji/Tone/GIF/AI → switch to Chat B → old workspace remains incorrectly active.

When the active editor/input session genuinely changes:
- Detect the change.
- Close/reset temporary workspace as appropriate.
- Return to the normal keyboard.
- Adapt to the new input type.

Persistent preferences must NOT be reset:
- Theme
- Keyboard height
- Toolbar arrangement
- Languages
- Sound
- Haptics
- Other user preferences

Do not reset too aggressively. Normal cursor movement, text changes, or minor callbacks should not unnecessarily close a workspace.

Audit the Android IME lifecycle and use appropriate InputConnection/editor lifecycle events.

Test:
- Chat A → Emoji → Chat B
- Chat A → Tone → Chat B
- Chat A → GIF → Chat B
- Chat A → AI → Chat B
- Switching apps
- Browser search fields
- Notes
- Email
- Password fields
- Numeric fields
- Keyboard hide/show
- Process recreation

============================================================
PHASE 9 — EMOJI MODE
============================================================

Emoji must replace the normal letter-key area.

Requirements:
- Emoji grid
- Search
- Categories
- Recently used
- Comfortable touch targets
- Smooth scrolling
- Immediate insertion
- No duplicate rapid insertion
- ABC/keyboard return control
- Stable keyboard height
- Responsive layout
- Dark/light theme compatibility

Emoji must not appear as a large panel above the keyboard.

============================================================
PHASE 10 — PREDICTIVE TEXT / PERSONAL LEARNING
============================================================

Implement/refine real next-word prediction.

Consider:
- Previous words
- Current sentence
- Word frequency
- Word combinations
- Sentence context
- Frequently used phrases
- Personal vocabulary
- Names
- Places
- Slang
- Abbreviations
- Capitalization
- Punctuation
- Language
- User behavior

Example:
"I will see you"
→ contextually predict words such as "tomorrow", "later", or "there".

Learn useful vocabulary over time:
- Words
- Phrases
- Names
- Slang
- Abbreviations
- Custom spellings

Repeatedly accepted suggestions may gain relevance.
Repeatedly rejected suggestions should lose relevance.

Allow:
- View learned words
- Delete individual words
- Clear learned vocabulary
- Disable learning
- Reset learning

Do not unnecessarily learn sensitive information.

============================================================
PHASE 11 — SMART SUGGESTION BAR
============================================================

Adapt suggestions to context.

Normal typing → word suggestions
After sentence → next-word suggestions
Selected text → editing/AI actions
Questions → contextual completions
Greetings → useful continuations
AI context → relevant AI actions

Do not overwhelm the user.

============================================================
PHASE 12 — SMART APOSTROPHE / CONTRACTION ENGINE
============================================================

The keyboard must intelligently handle missing apostrophes.

Examples:
im → I'm
ive → I've
ill → I'll
id → I'd
youre → you're
youve → you've
youll → you'll
hes → he's
shes → she's
thats → that's
whats → what's
wheres → where's
whos → who's
cant → can't
dont → don't
doesnt → doesn't
didnt → didn't
isnt → isn't
arent → aren't
wasnt → wasn't
werent → weren't
wont → won't
wouldnt → wouldn't
couldnt → couldn't
shouldnt → shouldn't
havent → haven't
hasnt → hasn't
hadnt → hadn't
mustnt → mustn't
lets → let's
theres → there's
theyre → they're
theyve → they've
were → do NOT incorrectly change valid words just because they resemble contractions.

Do NOT blindly insert apostrophes.

Use context, dictionary, sentence structure, language, user behavior, and confidence.

When confidence is uncertain, prefer a suggestion rather than aggressive replacement.

Respect autocorrect settings.

Support apostrophe characters:
'
and appropriate smart typography:
’

Do not duplicate apostrophes or create spaces around them.

Password fields and inappropriate input types must not receive unwanted transformations.

Integrate contractions with prediction so autocorrect and prediction do not fight each other.

============================================================
PHASE 13 — AI SYSTEM
============================================================

AI should complement ordinary typing.

Do NOT send every keystroke to an online AI.

Use local mechanisms for ordinary typing where possible.

AI actions can include:
- Rewrite
- Improve
- Grammar
- Formalize
- Simplify
- Expand
- Shorten
- Compose
- Reply
- Translate
- Summarize
- Explain
- Continue writing
- Tone adjustment

Handle:
- Loading
- Cancellation
- Timeout
- Network failure
- Invalid responses
- Rate limits
- Empty responses
- Retry
- Offline state
- Duplicate requests

============================================================
PHASE 14 — CLIPBOARD
============================================================

Support where appropriate:
- Clipboard history
- Search
- Pin
- Delete
- Clear all
- Quick paste
- Saved snippets
- Frequently used snippets

Protect sensitive/password fields.

============================================================
PHASE 15 — TEXT SHORTCUTS
============================================================

Allow:
- Create
- Edit
- Delete
- Disable
- Search

Example:
gm → Good morning
ty → Thank you
addr → saved snippet

============================================================
PHASE 16 — SMART CAPITALIZATION / PUNCTUATION
============================================================

Handle:
- Sentence beginnings
- Punctuation
- Proper names
- Acronyms
- User capitalization
- Quotes
- Apostrophes
- Brackets
- Spaces
- Double-space behavior
- Language-specific rules

Do not fight the user.

============================================================
PHASE 17 — SETTINGS
============================================================

Organize appropriately:
GENERAL
APPEARANCE
TYPING
SUGGESTIONS
AUTOCORRECT
LANGUAGES
AI
SOUND
HAPTICS
TOOLBAR
CLIPBOARD
PRIVACY
DATA
ACCESSIBILITY
ADVANCED
ABOUT

Every setting must actually work and persist where appropriate.

============================================================
PHASE 18 — COMPLETE APP STRUCTURE
============================================================

Provide appropriate:
- Splash/startup
- First-launch experience
- Onboarding
- Keyboard setup
- Enable keyboard guide
- Default keyboard guide
- Main settings
- Help
- FAQ
- Feedback
- Bug report
- Contact/support
- About
- Version information
- What's New
- Changelog
- Reset settings
- Restore defaults

Do not force accounts unless genuinely required.

============================================================
PHASE 19 — PRIVACY
============================================================

Because this is a keyboard, privacy is a first-class requirement.

Audit:
- Typed text
- Password fields
- Clipboard
- Learned words
- AI requests
- Network requests
- Local storage
- Logs
- Analytics
- Crash reports

Never unnecessarily collect or transmit typed information.

Provide appropriate controls for:
- Clear learned data
- Clear clipboard
- AI data settings
- Private/incognito typing
- Privacy information

Do not invent legal claims.

============================================================
PHASE 20 — SECURITY
============================================================

Audit:
- API keys
- Secrets
- Tokens
- Local storage
- Network communication
- Exported Android components
- Intents
- WebViews
- Authentication
- Logs
- Debug/release configuration
- Dependencies

Never put private API secrets directly into the APK.

============================================================
PHASE 21 — PERMISSIONS
============================================================

Audit every permission.

Only request permissions genuinely required.

Handle:
- Granted
- Denied
- Permanently denied
- Optional capability unavailable

The keyboard should remain usable without optional permissions.

============================================================
PHASE 22 — ERROR HANDLING
============================================================

No raw technical errors to users.

Handle:
- Network errors
- AI errors
- Timeouts
- Invalid responses
- Storage failures
- Database failures
- Permission failures
- Unexpected states

Provide:
- Loading states
- Empty states
- Error states
- Retry
- Recovery

============================================================
PHASE 23 — OFFLINE
============================================================

Core keyboard must remain useful offline.

When offline:
- Typing works
- Local functionality works
- Local prediction works where available
- Settings work
- Customization works
- AI clearly indicates unavailable network functionality
- No endless loading
- No crashes

============================================================
PHASE 24 — PERFORMANCE
============================================================

Audit:
- Startup
- Keyboard opening latency
- Typing latency
- Suggestion latency
- AI latency
- Memory
- CPU
- Battery
- Network

Prevent:
- Memory leaks
- UI blocking
- Excessive API calls
- Duplicate requests
- Freezes
- ANRs
- Excessive background work
- Excessive battery use

Typing must feel immediate.

============================================================
PHASE 25 — ANDROID IME INTEGRATION
============================================================

Treat this as a genuine Android Input Method Service.

Audit:
- InputConnection
- Editor actions
- Input types
- Password fields
- Email fields
- Numeric fields
- Search fields
- Multi-line fields
- Selection
- Cursor
- Composition
- Delete
- Enter
- IME actions
- Lifecycle
- Configuration changes

============================================================
PHASE 26 — LIFECYCLE
============================================================

Test:
- Launch
- Close
- Background
- Resume
- Keyboard switching
- Keyboard returning
- Rotation
- Configuration changes
- Process death
- Process recreation
- Network loss
- Network restoration

============================================================
PHASE 27 — UI/UX POLISH
============================================================

Audit:
- Typography
- Spacing
- Alignment
- Icons
- Touch targets
- Animations
- Transitions
- Loading
- Empty states
- Error states
- Dark mode
- Light mode
- Contrast
- Text overflow
- Small screens
- Large screens
- Landscape
- Tablets
- Foldables where appropriate

No unfinished-looking screens.

============================================================
PHASE 28 — ACCESSIBILITY
============================================================

Audit:
- TalkBack
- Content descriptions
- Touch targets
- Font scaling
- Contrast
- Reduced motion
- Screen readers
- Keyboard navigation where relevant

============================================================
PHASE 29 — INTERNATIONALIZATION
============================================================

Prepare for multiple languages.

Avoid hard-coded user-facing strings.

Support where appropriate:
- Translatable strings
- Localized settings
- Multiple languages
- Multiple layouts
- RTL
- Language-specific punctuation
- Language-specific capitalization

============================================================
PHASE 30 — DEVICE COMPATIBILITY
============================================================

Audit:
- Android versions
- Screen sizes
- Resolutions
- Aspect ratios
- Tablets
- Foldables
- Portrait
- Landscape
- Small devices
- Large devices

============================================================
PHASE 31 — ARCHITECTURE
============================================================

Audit separation between:
- UI
- IME/keyboard logic
- AI
- Networking
- Storage
- Settings
- Prediction
- Business logic

Avoid:
- Duplicate logic
- Circular dependencies
- Unnecessary complexity
- Dead code
- Unmaintainable systems

Preserve working architecture.

============================================================
PHASE 32 — DEPENDENCIES
============================================================

Audit:
- Outdated libraries
- Vulnerabilities
- Unused dependencies
- Duplicate dependencies
- License concerns
- Compatibility

Do not blindly upgrade everything.

============================================================
PHASE 33 — BUILD SYSTEM
============================================================

Audit:
- Gradle
- Build variants
- Debug/release separation
- Signing
- Version name
- Version code
- SDK configuration
- Manifest
- Resources
- R8/ProGuard where appropriate
- Release configuration

Debug behavior must not ship accidentally.

============================================================
PHASE 34 — DATA / DATABASE
============================================================

Audit:
- Preferences
- Learned vocabulary
- Clipboard
- User settings
- AI settings
- Database
- Schema
- Migrations
- Corruption handling
- Cleanup
- Reset
- Deletion

============================================================
PHASE 35 — UPDATE SAFETY
============================================================

Ensure future updates do not unnecessarily:
- Delete settings
- Corrupt data
- Lose learned vocabulary
- Break preferences
- Break database state

Implement migrations where necessary.

============================================================
PHASE 36 — BACKUP / RESTORE
============================================================

Audit whether data should be backed up.

Do not blindly back up sensitive keyboard information.

Safely restore appropriate non-sensitive settings where useful.

============================================================
PHASE 37 — TESTING
============================================================

Create/improve tests for:
- Keyboard initialization
- Key input
- Backspace
- Shift
- Space
- Symbols
- Long press
- Settings
- Persistence
- Prediction
- Learning
- Clipboard
- AI
- AI failure
- Network failure
- Offline mode
- Permissions
- Lifecycle
- Configuration changes
- Navigation
- Storage
- Database migrations
- Workspace switching
- Emoji
- Tone
- Apostrophe/contraction behavior

Never claim a test passed unless it actually ran and passed.

============================================================
PHASE 38 — DIAGNOSTICS
============================================================

Use useful developer diagnostics without logging sensitive keyboard content.

Never unnecessarily log:
- Passwords
- Typed text
- Private messages
- Sensitive clipboard content
- API secrets
- Authentication tokens

============================================================
PHASE 39 — CI/CD
============================================================

If appropriate, configure GitHub automation for:
- Build
- Tests
- Compilation checks
- Basic quality checks

Do not add unnecessary complexity.

============================================================
PHASE 40 — LEGAL / LICENSING
============================================================

Audit:
- Privacy Policy
- Terms where required
- Open-source licenses
- Fonts
- Icons
- Images
- APIs
- Third-party assets

Do not fabricate legal information.

============================================================
PHASE 41 — SUPPORT
============================================================

Provide appropriate:
- Help
- FAQ
- Feedback
- Bug reporting
- Contact
- Troubleshooting
- Version information
- Optional diagnostic information

Do not create fake support endpoints.

============================================================
PHASE 42 — RELEASE READINESS
============================================================

Audit:
- Application ID
- App name
- App icon
- Version
- Version code
- Release build
- Signing
- Manifest
- Permissions
- SDK configuration
- Privacy information
- Store requirements
- Debug code
- Debug logs
- Secrets
- Dependencies
- Resources

Do not claim production/store readiness unless relevant checks actually pass.

============================================================
PHASE 43 — THREE COMPLETE AUDITS
============================================================

AUDIT 1 — FEATURE AUDIT
Does every advertised feature actually work?

AUDIT 2 — PRODUCT AUDIT
Does this feel like a complete professional Android application?

AUDIT 3 — ENGINEERING AUDIT
Is the codebase maintainable, secure, performant, testable, and suitable for production?

============================================================
PHASE 44 — RECURSIVE AUDIT
============================================================

Do not stop at the first obvious problem.

Whenever you discover a problem:
1. Investigate it.
2. Fix it where appropriate.
3. Inspect connected systems.
4. Search for secondary problems.
5. Search for tertiary problems.
6. Verify the fix.
7. Continue.

Repeat until another complete audit finds no meaningful unresolved issues.

Do NOT invent problems merely to make the report longer.

============================================================
ABSOLUTE DEVELOPMENT RULES
============================================================

1. READ OLD FILES FIRST.
2. UNDERSTAND BEFORE MODIFYING.
3. NEVER BLINDLY OVERWRITE FILES.
4. NEVER REBUILD WORKING SYSTEMS WITHOUT A REASON.
5. NEVER CREATE DUPLICATE IMPLEMENTATIONS.
6. NEVER REMOVE WORKING FEATURES.
7. NEVER CREATE FAKE BUTTONS.
8. NEVER CREATE FAKE SETTINGS.
9. NEVER LEAVE PLACEHOLDERS DISGUISED AS FINISHED FEATURES.
10. NEVER HARD-CODE PRIVATE API SECRETS.
11. NEVER SEND EVERY KEYSTROKE TO AI UNNECESSARILY.
12. NEVER COMPROMISE NORMAL TYPING SPEED.
13. NEVER REQUEST UNNECESSARY PERMISSIONS.
14. NEVER FORCE AN ACCOUNT WITHOUT A REAL NEED.
15. NEVER ADD UNNECESSARY ANALYTICS.
16. NEVER ADD DEPENDENCIES WITHOUT JUSTIFICATION.
17. NEVER BLINDLY UPGRADE DEPENDENCIES.
18. NEVER CLAIM SOMETHING WAS TESTED WHEN IT WAS NOT.
19. NEVER CLAIM SOMETHING WORKS WHEN IT HAS NOT BEEN VERIFIED.
20. NEVER SACRIFICE PRIVACY FOR CONVENIENCE WITHOUT A CLEAR USER-CONTROLLED REASON.

============================================================
IMPLEMENTATION RULE
============================================================

After inspection and planning, IMPLEMENT the necessary changes in the existing project.

For every change:
1. Identify the relevant existing file.
2. Read it.
3. Understand it.
4. Modify it carefully.
5. Preserve existing functionality.
6. Build/check.
7. Fix errors.
8. Verify behavior.

Do not merely give me instructions. Actually implement the work.

============================================================
FINAL VERIFICATION
============================================================

After implementation:
1. Build the project.
2. Run available tests.
3. Fix compilation errors.
4. Fix reproducible runtime errors.
5. Re-check keyboard behavior.
6. Re-check settings.
7. Re-check persistence.
8. Re-check prediction.
9. Re-check learning.
10. Re-check AI failures.
11. Re-check offline behavior.
12. Re-check permissions.
13. Re-check privacy-sensitive areas.
14. Re-check lifecycle.
15. Re-check workspace behavior.
16. Re-check emoji mode.
17. Re-check Tone mode.
18. Re-check apostrophe/contraction behavior.
19. Re-check release configuration.
20. Perform all three audits again.

============================================================
FINAL REPORT
============================================================

When finished, report:

1. What you inspected.
2. What already existed.
3. What was missing.
4. What you implemented.
5. What you modified.
6. What you fixed.
7. What tests actually ran.
8. Which tests passed.
9. Which tests failed.
10. Remaining issues.
11. Required API/configuration values I must provide.
12. Manual Android/GitHub steps I must perform.
13. Remaining release blockers.

Do not hide unresolved problems.
Do not claim perfection without evidence.

============================================================
FINAL PRODUCT GOAL
============================================================

The goal is NOT simply:

"An AI keyboard with many features."

The goal is:

A POLISHED AI KEYBOARD
+
COMPLETE ANDROID APPLICATION
+
PROFESSIONAL UI/UX
+
MICRO-DETAILS
+
SMART NEXT-WORD PREDICTION
+
PERSONAL LEARNING
+
SMART CONTRACTION/APOSTROPHE HANDLING
+
AI WRITING TOOLS
+
INTEGRATED EMOJI WORKSPACE
+
INTEGRATED TONE WORKSPACE
+
RELIABLE ANDROID IME
+
PRIVACY
+
SECURITY
+
PERFORMANCE
+
ACCESSIBILITY
+
INTERNATIONALIZATION
+
MAINTAINABLE ARCHITECTURE
+
ROBUST ERROR HANDLING
+
TESTING
+
UPDATE SAFETY
+
RELEASE READINESS.

MOST IMPORTANT:

READ THE EXISTING PROJECT FIRST.
UNDERSTAND IT FIRST.
THEN MODIFY IT.

DO NOT BLINDLY REPLACE OLD FILES.

DO NOT STOP AT THE OBVIOUS FEATURES.

LOOK FOR THE SMALL DETAILS, ENGINEERING DETAILS, USER-EXPERIENCE DETAILS, AND PRODUCTION DETAILS THAT AN EXPERIENCED ANDROID/KEYBOARD ENGINEER WOULD CATCH.


=============================================================
EVERY-ANGLE ENGINEERING + PRODUCT + IME AUDIT
=============================================================

ROLE

Act as a senior Android engineer, keyboard/IME specialist, AI systems architect,
UX/product designer, accessibility engineer, security engineer, QA engineer,
performance engineer, database engineer, and release engineer.

Your job is not merely to add visible features. Inspect the complete existing
project, understand it, preserve what works, identify missing or fragile parts,
implement improvements safely, build and test them, and verify the final behavior.

READ THE EXISTING PROJECT BEFORE CHANGING ANYTHING.

Do not blindly overwrite files.
Do not create duplicate systems.
Do not rebuild working systems without a technical reason.
Do not create fake buttons/settings.
Do not claim tests passed when they were not run.
Do not claim production-ready without verification.

-------------------------------------------------------------
1. PROJECT FORENSICS
-------------------------------------------------------------

Inspect the entire repository before implementation:

- project structure
- Gradle configuration
- settings.gradle
- build.gradle files
- version catalogs
- AndroidManifest.xml
- source sets
- Kotlin/Java files
- Compose/XML layouts
- resources
- themes
- strings
- dimensions
- drawables
- navigation
- services
- repositories
- databases
- preferences
- ViewModels
- state management
- dependency injection
- networking
- AI integration
- InputMethodService
- InputConnection
- clipboard
- emoji
- symbols
- settings
- onboarding
- tests
- ProGuard/R8
- permissions
- localization
- accessibility

Map the architecture and data flow.

Identify:
1. What works.
2. What is incomplete.
3. What is UI-only.
4. What is hardcoded.
5. What is duplicated.
6. What is fragile.
7. What is unnecessary.
8. What is missing.
9. What can break.
10. What must not be changed unnecessarily.

-------------------------------------------------------------
2. ANDROID IME FOUNDATION
-------------------------------------------------------------

Audit the keyboard as a real Android Input Method Editor.

Verify:

- InputMethodService lifecycle
- onCreate
- onCreateInputView
- onStartInput
- onStartInputView
- onFinishInput
- onFinishInputView
- onDestroy
- onWindowShown
- onWindowHidden
- editor changes
- configuration changes
- InputConnection lifecycle
- currentInputConnection safety
- restartInput
- keyboard reopening
- application switching
- text-field switching
- process recreation
- activity recreation
- orientation changes
- background/foreground transitions
- IME visibility
- keyboard height
- window insets

Never assume an InputConnection remains valid.

Safely handle:
- null InputConnection
- stale InputConnection
- rejected commits
- cursor changes
- selection changes
- composition changes
- unexpected editor behavior

-------------------------------------------------------------
3. STATE ARCHITECTURE
-------------------------------------------------------------

Separate persistent state from temporary session state.

PERSISTENT:
- theme
- keyboard height
- language
- layout
- sound
- haptics
- toolbar arrangement
- autocorrect
- prediction
- privacy
- AI settings

SESSION:
- Emoji mode
- Symbols mode
- Tone mode
- AI mode
- GIF mode
- Clipboard mode
- Translate mode
- temporary search
- temporary selections
- temporary generated results

Session state should belong to the current editing/input context and reset
appropriately when switching to another editor/chat/text field.

Do not reset state merely because Android sends an unrelated lifecycle callback.

-------------------------------------------------------------
4. TYPING AND TOUCH INTELLIGENCE
-------------------------------------------------------------

Audit:

- key hitboxes
- touch tolerance
- adjacent-key correction
- edge touches
- touch slop
- rapid typing
- slow typing
- repeated taps
- long presses
- gesture conflicts
- toolbar touch targets
- accidental taps

The keyboard should be forgiving without creating excessive wrong characters.

-------------------------------------------------------------
5. KEY FEEDBACK
-------------------------------------------------------------

Audit:

- key highlight
- press/release animation
- long-press animation
- popup preview
- alternate-character popup
- sound
- vibration
- timing
- disabled states
- reduced-motion behavior

Animations must never introduce noticeable typing latency.

-------------------------------------------------------------
6. BACKSPACE ENGINE
-------------------------------------------------------------

Support and test:

- single-character deletion
- continuous deletion
- acceleration
- word deletion where appropriate
- punctuation-aware deletion
- whitespace-aware deletion
- emoji sequence deletion
- composing-text deletion
- selection deletion
- deletion around cursor
- autocorrection recovery

Never delete unexpected text because of an incorrect cursor assumption.

-------------------------------------------------------------
7. SPACEBAR
-------------------------------------------------------------

Audit:

- normal spaces
- double-space punctuation if enabled
- punctuation spacing
- quotation spacing
- bracket spacing
- emoji spacing
- suggestion insertion spacing
- trailing spaces
- repeated spaces
- language-specific spacing

-------------------------------------------------------------
8. CAPITALIZATION AND PUNCTUATION
-------------------------------------------------------------

Support context-aware:

- sentence capitalization
- punctuation capitalization
- newline capitalization
- names where appropriate
- acronyms
- all-caps
- manual Shift
- language-specific rules

Do not aggressively alter valid text.

-------------------------------------------------------------
9. SMART CONTRACTIONS
-------------------------------------------------------------

Support context-aware transformations such as:

im → I'm
dont → don't
cant → can't
wont → won't
youre → you're
theyre → they're
ive → I've
ill → I'll
didnt → didn't
doesnt → doesn't
isnt → isn't

Never blindly modify valid words.

Respect:
- language
- dictionary
- context
- input type
- user settings
- autocorrect settings

Provide an easy undo path.

-------------------------------------------------------------
10. AUTOCORRECT
-------------------------------------------------------------

Audit:

- spelling correction
- contextual correction
- confidence thresholds
- user dictionary
- learned vocabulary
- names
- slang
- abbreviations
- technical terms
- multilingual words
- accepted/rejected corrections
- undo behavior

Never turn autocorrect into uncontrollable rewriting.

-------------------------------------------------------------
11. PREDICTION AND PERSONAL LEARNING
-------------------------------------------------------------

Support intelligent next-word prediction using appropriate signals:

- current word
- previous words
- sentence structure
- punctuation
- accepted suggestions
- rejected suggestions
- vocabulary
- phrases
- names
- abbreviations
- slang
- typing patterns
- language

Keep prediction fast.

Do not send every keystroke to remote AI.

If learning is enabled, provide controls to:
- disable learning
- clear learned vocabulary
- reset predictions
- manage personal dictionary

Do not unnecessarily learn sensitive information.

-------------------------------------------------------------
12. EMOJI SYSTEM
-------------------------------------------------------------

Emoji mode should replace the normal key area rather than appearing as an
unnecessarily large panel above the keyboard.

Support:

- categories
- search
- recent emoji
- frequently used emoji
- skin tones
- variants
- Unicode compatibility
- immediate insertion
- backspace
- ABC return
- smooth scrolling
- empty states

Keep behavior consistent with the intended persistent emoji workspace.

-------------------------------------------------------------
13. SYMBOL SYSTEM
-------------------------------------------------------------

Support a comprehensive symbol system:

- currencies
- mathematics
- arrows
- brackets
- operators
- technical symbols
- Greek letters
- fractions
- superscripts
- subscripts
- punctuation
- scientific symbols
- miscellaneous Unicode symbols

Include currencies such as:
$, €, £, ¥, ₩, ₹, ₽, ₺, ₦, ₵, ₡, ₫, ₱, ₲, ₴, ₸, ₼, ฿, ₿

Support categories, recent symbols, long press, insertion, and return to letters.

-------------------------------------------------------------
14. TONE WORKSPACE
-------------------------------------------------------------

Tone mode should replace the normal key area.

Support:
- Casual
- Formal
- Funny
- Professional
- Friendly
- Polite
- Confident
- Creative
- Serious
- Short
- Flirty
- Custom

Tone mode should remain active when appropriate instead of disappearing after one action.

-------------------------------------------------------------
15. AI WRITING SYSTEM
-------------------------------------------------------------

Support:

- Rewrite
- Improve
- Grammar
- Formalize
- Simplify
- Expand
- Shorten
- Compose
- Reply
- Translate
- Summarize
- Explain
- Continue writing
- Tone adjustment
- Make clearer
- Make more natural

Correctly handle selected text, cursor position, surrounding text, and current
editing context where available.

Never transmit sensitive text unnecessarily.

-------------------------------------------------------------
16. AI RELIABILITY
-------------------------------------------------------------

Handle:

- no internet
- timeout
- API failure
- invalid API key
- rate limits
- malformed responses
- empty responses
- server errors
- cancellation
- duplicate requests
- stale responses
- partial responses
- unavailable provider
- quota exhaustion

Use request IDs/cancellation/state validation where appropriate.

Never leave the keyboard permanently loading.

Never expose API secrets in the APK.

-------------------------------------------------------------
17. CLIPBOARD
-------------------------------------------------------------

Support:

- clipboard history
- search
- pin
- delete
- clear all
- snippets
- insertion
- preview
- privacy controls
- sensitive clipboard handling

Respect Android clipboard restrictions.

-------------------------------------------------------------
18. TEXT SHORTCUTS
-------------------------------------------------------------

Support custom shortcuts.

Allow:
- create
- edit
- delete
- search
- enable/disable
- import/export where appropriate

Prevent shortcut loops.

-------------------------------------------------------------
19. TEXT EDITING
-------------------------------------------------------------

Audit:

- cursor movement
- selection
- select all
- cut
- copy
- paste
- replace
- selection deletion
- insertion at cursor
- AI replacement
- emoji insertion
- symbol insertion

Never assume the cursor is at the end.

-------------------------------------------------------------
20. INPUT-FIELD ADAPTATION
-------------------------------------------------------------

Adapt appropriately to:

- normal text
- multiline
- email
- URL
- phone
- numeric
- password
- PIN
- search
- username
- address
- date/time
- decimal
- signed number
- web fields

Do not apply inappropriate AI/prediction behavior to sensitive fields.

-------------------------------------------------------------
21. LONG-PRESS SYSTEM
-------------------------------------------------------------

Audit every long press.

Examples:

- comma → Settings
- period → alternate punctuation
- letters → alternate characters
- numbers → symbols
- currency → variants
- backspace → accelerated deletion
- emoji → variants
- space → appropriate language/cursor behavior

Normal taps must continue to perform their normal actions.

-------------------------------------------------------------
22. TOOLBAR
-------------------------------------------------------------

Toolbar should support appropriate:

- customization
- reordering
- hiding/showing
- persistence
- responsive layout
- accessibility

Potential tools:

- AI
- Emoji
- GIF
- Clipboard
- Tone
- Symbols
- Translate
- Settings

Prevent overflow and accidental taps.

-------------------------------------------------------------
23. TOOL SWITCHING
-------------------------------------------------------------

Switching between:

Letters
Emoji
Symbols
AI
Tone
GIF
Clipboard
Translate

must be predictable.

Avoid:
- lost searches
- unexpected resets
- flickering
- keyboard height jumps
- broken InputConnection
- lost text

-------------------------------------------------------------
24. APP/CHAT SWITCHING
-------------------------------------------------------------

Test switching between messaging apps, browsers, notes, email, search fields,
social apps, and password fields.

Persistent preferences remain intact.

Temporary workspace state follows the current editing context.

-------------------------------------------------------------
25. SETTINGS
-------------------------------------------------------------

Use coherent categories such as:

General
Appearance
Typing
Suggestions
Autocorrect
Languages
AI
Sound
Haptics
Toolbar
Clipboard
Privacy
Data
Accessibility
Advanced
About

Every setting must connect to real behavior.

NO FAKE SETTINGS.

-------------------------------------------------------------
26. APPEARANCE AND UX
-------------------------------------------------------------

Audit:

- themes
- dark/light/system mode
- key shape
- key spacing
- height
- font size
- toolbar
- suggestion bar
- animation
- icon consistency
- contrast
- visual hierarchy
- screen-size adaptation

Fix clipping, cramped controls, excessive empty space, inconsistent padding,
typography, icons, corner radii, and visual noise.

-------------------------------------------------------------
27. ACCESSIBILITY
-------------------------------------------------------------

Test:

- TalkBack
- content descriptions
- focus order
- touch targets
- contrast
- large text
- reduced motion
- haptic alternatives
- non-color state communication

-------------------------------------------------------------
28. INTERNATIONALIZATION
-------------------------------------------------------------

Support architecture for:

- different alphabets
- RTL
- language switching
- punctuation differences
- spacing differences
- capitalization rules
- multilingual typing
- language-specific predictions
- language-specific autocorrect

Never assume English rules apply everywhere.

-------------------------------------------------------------
29. PERFORMANCE
-------------------------------------------------------------

Measure and optimize:

- startup
- first render
- key latency
- prediction latency
- scrolling
- emoji search
- clipboard
- database
- AI
- memory
- CPU
- battery
- animations

Do not block the main thread.

Avoid memory leaks and unnecessary recompositions/rerenders.

-------------------------------------------------------------
30. OFFLINE MODE
-------------------------------------------------------------

Basic typing must remain usable without internet.

Separate local keyboard functionality from network-dependent AI features.

Network failure must never disable ordinary typing.

-------------------------------------------------------------
31. DATA AND STORAGE
-------------------------------------------------------------

Audit:

- preferences
- database
- migrations
- corruption handling
- backup
- restore
- clearing data
- upgrades
- uninstall/reinstall behavior

Do not lose user settings during normal updates.

-------------------------------------------------------------
32. SECURITY AND PRIVACY
-------------------------------------------------------------

Audit:

- API keys
- secrets
- logs
- clipboard
- typed text
- passwords
- network traffic
- databases
- analytics
- crash reports
- debug logging

Never log passwords or sensitive typed content.

Never hardcode private API secrets.

Transmit only what is necessary.

-------------------------------------------------------------
33. PERMISSIONS
-------------------------------------------------------------

Request only genuinely required permissions.

For each permission:

- explain purpose
- handle denial
- handle permanent denial
- provide fallback

-------------------------------------------------------------
34. ERROR HANDLING
-------------------------------------------------------------

Every subsystem needs:

- validation
- clear error state
- recovery
- feedback
- retry where appropriate
- safe fallback

No blank screens, dead buttons, infinite spinners, silent failures, or corrupted state.

-------------------------------------------------------------
35. ONBOARDING AND SUPPORT
-------------------------------------------------------------

Provide a clear first-run flow covering:

- what the keyboard does
- enabling it
- selecting it as default
- privacy implications
- AI setup
- major features
- customization

Also support:

- Help
- FAQ
- Troubleshooting
- Feedback
- Bug report
- Contact
- About
- Version
- What's New
- Changelog
- Reset settings
- Restore defaults

-------------------------------------------------------------
36. TESTING
-------------------------------------------------------------

Actually test what can be tested.

Test:

- unit
- UI
- IME
- lifecycle
- text insertion
- deletion
- selection
- autocorrect
- prediction
- emoji
- symbols
- AI
- clipboard
- settings
- persistence
- rotation
- process recreation
- offline mode
- errors
- accessibility
- performance
- different screen sizes

Test expected behavior AND bad/unusual user behavior.

-------------------------------------------------------------
37. REGRESSION
-------------------------------------------------------------

Before modifying an existing feature:

UNDERSTAND IT.

After modifying it:

VERIFY PREVIOUS FUNCTIONALITY.

Never fix one subsystem by silently breaking another.

-------------------------------------------------------------
38. BUILD AND RELEASE VERIFICATION
-------------------------------------------------------------

After implementation:

1. Build.
2. Run available tests.
3. Inspect compiler errors.
4. Inspect warnings.
5. Fix issues.
6. Rebuild.
7. Test again.
8. Verify resources.
9. Verify dependencies.
10. Verify manifest.
11. Verify release configuration.

Never claim testing that did not happen.

Audit:

- application ID
- version code/name
- release build
- signing
- R8/ProGuard
- resource shrinking
- min/target SDK
- permissions
- debug code
- test keys
- placeholder content
- TODOs
- mock implementations

-------------------------------------------------------------
39. CODE QUALITY
-------------------------------------------------------------

Audit:

- naming
- architecture
- separation of concerns
- lifecycle safety
- state management
- null safety
- concurrency
- coroutines
- threading
- resource management
- dependencies
- duplicated code
- dead code
- magic numbers
- hardcoded strings
- maintainability

Prefer reliable simplicity over unnecessary complexity.

-------------------------------------------------------------
40. DEVICE COMPATIBILITY
-------------------------------------------------------------

Consider:

- Android versions
- small screens
- large screens
- different aspect ratios
- low-memory devices
- font scales
- dark/light mode
- navigation modes
- display cutouts
- gesture navigation
- keyboard height variations
- manufacturer-specific behavior

=============================================================
FINAL MICRO-POLISH / PETTY-DETAIL AUDIT
=============================================================

Do not look only for missing features.

Examine every interaction a person performs repeatedly:

- pressing a key
- holding a key
- deleting
- spacing
- correcting
- selecting
- moving the cursor
- opening emoji
- opening symbols
- opening tone
- opening AI
- switching tools
- changing chats
- changing applications
- hiding/reopening the keyboard
- rotating the device
- losing/recovering network
- dismissing an error
- reopening a previous workspace

Look for tiny friction points that make the keyboard feel unfinished.

Audit:

- key press animation
- popup preview
- long-press animation
- key hitboxes
- adjacent-key correction
- haptics
- sounds
- backspace repeat
- backspace acceleration
- spacebar behavior
- capitalization
- punctuation
- suggestion ranking
- emoji search
- emoji recents
- symbol recents
- tone persistence
- AI loading
- AI cancellation
- tool switching
- cursor behavior
- selection
- input-field adaptation
- lifecycle
- visual alignment
- icon sizing
- text baseline
- spacing
- scrolling
- empty states
- error states
- loading states
- accessibility
- settings feedback
- performance
- privacy behavior

Ask repeatedly:

"What would annoy someone who uses this keyboard for hours every day?"

Fix legitimate issues without breaking existing architecture.

=============================================================
MASTER FILE-SAFETY RULE
=============================================================

Before modifying any file:

READ THE COMPLETE RELEVANT FILE.

Determine the smallest safe modification.

Do not overwrite an entire file unnecessarily.

Do not create duplicate:

- Activities
- Services
- ViewModels
- repositories
- settings systems
- AI clients
- state systems
- keyboard implementations

unless technically required.

=============================================================
IMPLEMENTATION WORKFLOW
=============================================================

ALWAYS FOLLOW:

READ
→ UNDERSTAND
→ MAP
→ IDENTIFY
→ PRIORITIZE
→ IMPLEMENT
→ BUILD
→ TEST
→ VERIFY
→ REGRESSION TEST
→ MICRO-POLISH
→ FINAL AUDIT

Before every change ask:

1. Does this already exist?
2. Does it already work?
3. Can the existing implementation be improved instead?
4. Will this conflict with another subsystem?
5. Does it affect InputConnection?
6. Does it affect IME lifecycle?
7. Does it affect performance?
8. Does it affect privacy?
9. Does it affect accessibility?
10. Does it affect existing settings?

=============================================================
OUTPUT RULE
=============================================================

When changes are required, first report:

1. What you inspected.
2. What you found.
3. What needs changing.
4. Which files need changing.
5. Why each file needs changing.

Then implement.

If a complete file is requested, provide the COMPLETE final file, not an unexplained fragment,
unless a patch is specifically requested.

Never make the developer guess where code belongs.

=============================================================
NO-FALSE-COMPLETION RULE
=============================================================

Never claim:

"Everything is perfect."
"Fully tested."
"Guaranteed working."
"Production ready."

unless evidence supports the statement.

Report honestly:

IMPLEMENTED
VERIFIED
NOT VERIFIED
KNOWN LIMITATIONS
REMAINING ISSUES

=============================================================
FINAL COMMAND
=============================================================

Do not stop when the keyboard merely builds.

Do not stop when the keyboard merely appears.

Do not stop when buttons merely work.

Continue until the system has been audited at every relevant layer.

READ THE EXISTING PROJECT FIRST.

UNDERSTAND THE ARCHITECTURE.

DO NOT GUESS.

DO NOT DUPLICATE.

DO NOT BREAK WORKING FEATURES.

AUDIT EVERY LAYER.

IMPLEMENT CAREFULLY.

BUILD.

TEST.

FIX.

REBUILD.

VERIFY.

REGRESSION TEST.

PERFORM THE PETTY-DETAIL AUDIT.

THEN PERFORM ONE FINAL PASS SPECIFICALLY LOOKING FOR SOMETHING YOU MISSED.


=============================================================
FINAL EVERYTHING LAYER — DEEP SYSTEM AUDIT
=============================================================

IMPORTANT:
Add these requirements to the existing master specification.
Do NOT implement swipe typing or swipe/gesture word input.
Normal taps, long presses, scrolling, cursor/text editing, and other explicitly
requested interactions remain allowed.

-------------------------------------------------------------
1. KEYBOARD INTELLIGENCE
-------------------------------------------------------------

Audit the intelligence layer for:

- prediction confidence
- suggestion ranking
- context modeling
- typo-pattern recognition
- accepted/rejected suggestion learning
- user vocabulary
- phrase prediction
- abbreviation recognition
- slang recognition
- names and custom words
- context-aware corrections
- confidence thresholds
- fallback behavior when confidence is low

Do not make predictions so aggressive that they become annoying.

-------------------------------------------------------------
2. LANGUAGE ENGINE
-------------------------------------------------------------

Audit:

- tokenization
- word boundaries
- punctuation boundaries
- Unicode handling
- combining characters
- compound words
- contractions
- abbreviations
- multilingual text
- transliteration where supported
- locale-specific capitalization
- locale-specific punctuation
- RTL text
- mixed-language sentences

Never assume one language's grammar rules apply to every language.

-------------------------------------------------------------
3. UNICODE AND TEXT-ENGINE CORRECTNESS
-------------------------------------------------------------

Correctly handle:

- Unicode code points
- UTF-16 behavior on Android
- surrogate pairs
- combining marks
- zero-width joiners
- zero-width characters
- emoji sequences
- skin-tone modifiers
- gender variants
- flags
- variation selectors
- composed/decomposed characters
- normalization where appropriate

Backspace must delete a user-perceived character correctly rather than
blindly deleting one UTF-16 code unit.

-------------------------------------------------------------
4. ADVANCED IME INTERNALS
-------------------------------------------------------------

Audit Android editor interaction including, where supported:

- composing text
- commitText
- setComposingText
- finishComposingText
- deleteSurroundingText
- deleteSurroundingTextInCodePoints
- getTextBeforeCursor
- getTextAfterCursor
- getSelectedText
- setSelection
- beginBatchEdit
- endBatchEdit
- editor capabilities
- selection updates
- cursor updates
- extracted text
- fullscreen extract mode

Use the correct API for the operation instead of approximating text manipulation.

-------------------------------------------------------------
5. UNUSUAL EDITORS AND APP COMPATIBILITY
-------------------------------------------------------------

Test against:

- standard Android EditText
- Compose text fields
- WebView fields
- browser search fields
- messaging applications
- email applications
- note applications
- rich-text editors
- custom text editors
- unusual/broken InputConnection implementations

Gracefully degrade when an editor does not support a requested operation.

-------------------------------------------------------------
6. AI ORCHESTRATION
-------------------------------------------------------------

If multiple AI providers/models are supported, architect:

- provider abstraction
- model selection
- fallback providers
- timeout handling
- retry strategy
- request cancellation
- request IDs
- response validation
- provider health handling
- model capability matching
- rate-limit handling
- cost controls
- caching where safe
- prompt versioning

Never allow an older response to overwrite newer user work.

-------------------------------------------------------------
7. AI OUTPUT VALIDATION
-------------------------------------------------------------

Before inserting AI-generated text:

- validate that a response exists
- validate expected response format
- detect malformed output
- detect unexpected metadata
- preserve the user's intended meaning where possible
- ensure selected text is replaced correctly
- ensure cursor placement is correct
- prevent duplicate insertion
- allow cancellation
- allow recovery from failure

AI output must never be treated as automatically correct.

-------------------------------------------------------------
8. AI PROMPT-INJECTION RESISTANCE
-------------------------------------------------------------

Treat user-provided text, clipboard text, web content, pasted content and
other external text as untrusted input.

Do not allow text being processed by the AI to silently override higher-level
application instructions.

Never expose:

- hidden system instructions
- API credentials
- internal configuration
- private logs
- unrelated user data

-------------------------------------------------------------
9. CONCURRENCY AND RACE CONDITIONS
-------------------------------------------------------------

Test situations such as:

- AI request A starts
- AI request B starts
- B finishes first
- A finishes later

The stale result must not overwrite newer state.

Also test:

- rapid button presses
- rapid tool switching
- keyboard closing during a request
- app switching during a request
- process recreation during a request
- database writes occurring simultaneously
- settings changing during an operation

Use appropriate cancellation, synchronization and state validation.

-------------------------------------------------------------
10. CRASH AND ANR RESILIENCE
-------------------------------------------------------------

Audit for:

- null crashes
- lifecycle crashes
- threading errors
- coroutine cancellation problems
- main-thread blocking
- ANRs
- memory leaks
- resource leaks
- invalid state transitions

Long operations must not block keyboard input.

The keyboard must remain responsive while background work occurs.

-------------------------------------------------------------
11. PROCESS-DEATH RECOVERY
-------------------------------------------------------------

Test:

- keyboard process killed by Android
- app process killed
- low-memory process recreation
- device restart
- force-stop followed by reopening

Restore appropriate persistent state without restoring stale temporary state.

-------------------------------------------------------------
12. DATABASE AND MIGRATION ENGINEERING
-------------------------------------------------------------

If persistent storage is used:

- version schemas
- create migrations
- test upgrades
- test downgrade behavior where relevant
- handle migration failure
- handle corrupted records
- handle empty databases
- avoid data loss
- preserve learned vocabulary where appropriate
- preserve shortcuts
- preserve settings

Never assume the database will always be perfect.

-------------------------------------------------------------
13. BACKUP AND RESTORE
-------------------------------------------------------------

If backup/restore is supported, define exactly what is backed up.

Potentially restorable:

- settings
- themes
- toolbar arrangement
- shortcuts
- user dictionary
- non-sensitive preferences

Do NOT casually back up sensitive typed content or other private data.

Test:

- backup
- restore
- incompatible version
- partial restore
- corrupted backup
- missing fields

-------------------------------------------------------------
14. BATTERY ENGINEERING
-------------------------------------------------------------

Audit:

- background work
- AI/network activity
- timers
- observers
- database polling
- repeated recomputation
- unnecessary wakeups
- resource-heavy animations

The keyboard must not consume excessive battery simply because it remains
available as an IME.

-------------------------------------------------------------
15. MEMORY ENGINEERING
-------------------------------------------------------------

Check for:

- memory leaks
- oversized caches
- unnecessary bitmap retention
- excessive emoji resources
- clipboard growth
- AI response accumulation
- lifecycle references
- unnecessary objects
- unbounded lists

Test under memory pressure.

-------------------------------------------------------------
16. THERMAL/RESOURCE BEHAVIOR
-------------------------------------------------------------

Avoid unnecessarily intensive:

- CPU work
- rendering
- animations
- network activity
- prediction computation

The keyboard should remain responsive on lower-end devices.

-------------------------------------------------------------
17. CACHING STRATEGY
-------------------------------------------------------------

Audit caching for:

- emoji
- symbols
- settings
- AI responses where appropriate
- network metadata
- images/GIFs if supported

Every cache must have:

- size limits
- invalidation rules
- lifecycle behavior
- privacy considerations

Never cache sensitive text indiscriminately.

-------------------------------------------------------------
18. MEDIA / GIF / STICKER SYSTEM
-------------------------------------------------------------

If these features exist, audit:

- search
- loading
- pagination
- caching
- bandwidth
- provider failures
- unsupported apps
- MIME types
- insertion compatibility
- cancellation
- empty results
- retry

Do not assume every receiving app supports every media type.

-------------------------------------------------------------
19. VOICE INPUT
-------------------------------------------------------------

If voice input exists, audit:

- microphone permission
- start/stop
- cancellation
- transcription errors
- punctuation
- language
- offline behavior
- network failures
- insertion position
- privacy
- recording state
- accidental activation

-------------------------------------------------------------
20. UNDO / REDO
-------------------------------------------------------------

Where technically appropriate, support reliable recovery for:

- autocorrection
- AI replacement
- generated text
- pasted content
- deletion
- accidental replacement

Do not implement an unsafe fake undo system that cannot accurately restore state.

-------------------------------------------------------------
21. THREAT MODELING
-------------------------------------------------------------

Perform a security review covering:

- malicious applications
- compromised network
- malicious clipboard content
- malicious AI responses
- leaked API credentials
- insecure local storage
- debug builds
- exported components
- dependency vulnerabilities
- excessive permissions
- sensitive logs
- accidental text transmission

Document meaningful risks and mitigations.

-------------------------------------------------------------
22. DEPENDENCY SECURITY
-------------------------------------------------------------

Audit dependencies for:

- known vulnerabilities
- outdated versions
- unnecessary libraries
- incompatible versions
- abandoned libraries
- excessive permissions
- license restrictions

Do not introduce a dependency without understanding why it is needed.

-------------------------------------------------------------
23. LEGAL AND LICENSING
-------------------------------------------------------------

Audit third-party:

- libraries
- fonts
- icons
- images
- emoji resources
- GIF providers
- sticker providers
- AI services
- SDKs

Track applicable licenses and attribution requirements.

Do not copy proprietary code or assets without permission.

-------------------------------------------------------------
24. ANALYTICS AND OBSERVABILITY
-------------------------------------------------------------

If analytics are intentionally used:

- minimize collected data
- never collect typed content unnecessarily
- never collect passwords
- respect privacy settings
- separate diagnostics from content
- document what is collected

For internal diagnostics, provide useful non-sensitive information such as:

- feature state
- error type
- timing
- lifecycle state
- performance metrics

-------------------------------------------------------------
25. DEBUG/DIAGNOSTICS MODE
-------------------------------------------------------------

Where appropriate, create a controlled developer diagnostics system that can
show:

- IME lifecycle state
- InputConnection availability
- current editor type
- prediction latency
- AI request status
- database status
- memory information
- feature flags
- error identifiers

Never expose sensitive typed content in diagnostics.

Ensure debug features are disabled or secured in release builds.

-------------------------------------------------------------
26. REAL-DEVICE COMPATIBILITY
-------------------------------------------------------------

Consider testing across:

- Samsung
- Tecno
- Infinix
- Xiaomi
- Oppo
- Vivo
- Google Pixel
- other common Android manufacturers

Also consider:

- different Android versions
- low-RAM devices
- different aspect ratios
- gesture navigation
- three-button navigation
- display cutouts
- font scaling
- dark/light modes

Do not assume behavior on one device represents all devices.

-------------------------------------------------------------
27. KEYBOARD LAYOUT ENGINE
-------------------------------------------------------------

If multiple layouts are supported, architect:

- QWERTY
- QWERTZ
- AZERTY
- numeric layouts
- symbol layouts
- language-specific layouts

Layouts must not break:

- touch targets
- capitalization
- prediction
- long press
- toolbar
- accessibility
- keyboard height

-------------------------------------------------------------
28. ONE-HANDED AND ERGONOMIC USE
-------------------------------------------------------------

Consider:

- one-handed layout options
- reachable toolbar controls
- keyboard height
- key spacing
- accidental edge touches
- landscape mode
- small-screen devices

Do not sacrifice accessibility or typing accuracy merely to make the keyboard compact.

-------------------------------------------------------------
29. ACCESSIBILITY DEPTH
-------------------------------------------------------------

Go beyond basic TalkBack.

Consider:

- motor accessibility
- reduced motion
- large text
- high contrast
- touch target sizing
- vibration sensitivity
- audio feedback alternatives
- one-handed use
- screen magnification
- focus behavior

-------------------------------------------------------------
30. USER CUSTOMIZATION ARCHITECTURE
-------------------------------------------------------------

Where appropriate support:

- custom themes
- keyboard height
- key size
- spacing
- toolbar configuration
- key actions
- language layouts
- shortcuts
- profiles
- import/export
- reset individual settings
- restore defaults

Customization must not create invalid keyboard configurations.

-------------------------------------------------------------
31. RELEASE ENGINEERING
-------------------------------------------------------------

Audit:

- debug vs release configuration
- signing
- R8/ProGuard
- resource shrinking
- versioning
- build reproducibility
- dependency locking where appropriate
- release notes
- changelog
- crash reporting
- privacy documentation
- store metadata
- permissions justification
- data-safety declarations where applicable

-------------------------------------------------------------
32. AUTOMATED QUALITY GATES
-------------------------------------------------------------

Where the project supports them, run:

- compilation
- unit tests
- instrumentation tests
- UI tests
- lint
- static analysis
- dependency checks
- resource validation
- release build
- APK inspection

Do not report a quality gate as passed unless it actually ran successfully.

-------------------------------------------------------------
33. FUZZ / STRESS TESTING
-------------------------------------------------------------

Stress important systems with unusual sequences:

- rapid typing
- repeated deletion
- repeated tool switching
- rapid opening/closing
- empty text
- very long text
- large selections
- huge clipboard history
- malformed AI responses
- repeated AI cancellation
- network switching
- process recreation
- low memory

Look for crashes, freezes, corrupted state and stale UI.

-------------------------------------------------------------
34. FAILURE-MATRIX TESTING
-------------------------------------------------------------

For every major feature explicitly consider:

SUCCESS PATH
FAILURE PATH
CANCELLATION PATH
TIMEOUT PATH
OFFLINE PATH
PERMISSION-DENIED PATH
LIFECYCLE PATH
PROCESS-DEATH PATH
RECOVERY PATH
PERSISTENCE PATH
ACCESSIBILITY PATH
PRIVACY PATH
PERFORMANCE PATH
REGRESSION PATH

A feature is not complete until these paths have been considered.

-------------------------------------------------------------
35. PRODUCT-QUALITY REVIEW
-------------------------------------------------------------

After technical testing, review the keyboard as a real user would.

Ask:

- Is the purpose of each feature obvious?
- Can users discover features?
- Are settings understandable?
- Are errors understandable?
- Does the keyboard feel fast?
- Does it behave predictably?
- Does it recover gracefully?
- Does it feel consistent?
- Are there unnecessary steps?
- Are common actions easy?
- Are uncommon actions still discoverable?

Fix genuine usability problems without adding unnecessary complexity.

=============================================================
FINAL RESTRICTION
=============================================================

DO NOT IMPLEMENT SWIPE TYPING OR SWIPE-TO-TYPE WORD INPUT.

Do not add gesture-based word entry as a hidden feature, fallback, experiment,
or optional mode unless explicitly requested in a future instruction.

Ordinary touch interaction, long press, scrolling, cursor/text editing and
other explicitly specified interactions remain allowed.

=============================================================
ULTIMATE FINAL AUDIT
=============================================================

After all implementation and testing, perform one final independent audit.

Do not simply reread your own changes.

Pretend you are:

1. A normal user.
2. A heavy daily keyboard user.
3. A developer maintaining the project six months later.
4. A QA engineer trying to break it.
5. A privacy/security reviewer.
6. An accessibility user.
7. A low-end Android device user.
8. A user with poor internet.
9. A user switching rapidly between apps.
10. A user who makes many typing mistakes.

Find and fix legitimate problems.

The final standard is:

FAST.
STABLE.
PREDICTABLE.
INTELLIGENT.
PRIVATE.
ACCESSIBLE.
RECOVERABLE.
MAINTAINABLE.
POLISHED.

Do not stop at "it builds."

Do not stop at "the screen opens."

Do not stop at "the button works."

Trace the complete flow from:

USER TOUCH
→ UI STATE
→ KEYBOARD LOGIC
→ IME SERVICE
→ INPUT CONNECTION
→ TEXT EDITOR
→ PERSISTENCE/AI/NETWORK WHEN NEEDED
→ RESULT
→ RECOVERY

Then test what happens when every stage fails.

FINAL COMMAND:

READ.
UNDERSTAND.
AUDIT.
PRESERVE.
IMPLEMENT.
BUILD.
TEST.
BREAK IT.
FIX IT.
REBUILD.
VERIFY.
REGRESSION TEST.
MICRO-POLISH.
SECURITY REVIEW.
PERFORMANCE REVIEW.
ACCESSIBILITY REVIEW.
FINAL EVERYTHING AUDIT.

Only then report the actual verified status.


============================================================
EXISTING API / COST CONTROL — FINAL PROJECT RULE
============================================================

IMPORTANT PROJECT CONTEXT:
I already have an API available for this project. Use my existing API where it is appropriate for the AI features.

Do NOT:
- Replace my existing API with another paid API without my explicit approval.
- Add unnecessary paid APIs, subscriptions, cloud services, or usage-based services.
- Assume I have billing enabled for any additional provider.
- Create hidden recurring costs.
- Hard-code API keys, secrets, tokens, passwords, or credentials into source code.
- Commit secrets to GitHub.
- Put private API credentials inside an Android APK where they can be extracted.
- Create unnecessary background API requests that could consume my API quota.
- Send every keystroke to the AI API.
- Make AI-dependent features crash or disable the entire keyboard when the API is unavailable.

DO:
1. Inspect the existing project and determine how the existing API is currently configured, if applicable.
2. Reuse the existing API architecture when it is safe and technically appropriate.
3. Keep the API integration modular so the provider can be changed later without rewriting the keyboard.
4. Keep the core keyboard functional even when the API is unavailable.
5. Handle API timeout, rate limits, network failure, invalid responses, authentication errors, server errors, cancellation, and malformed data gracefully.
6. Avoid unnecessary API calls through sensible debouncing, batching, caching, request cancellation, and request limits where appropriate.
7. Never expose the API key in logs, crash reports, UI, GitHub, source control, or generated screenshots.
8. Use secure configuration appropriate to the actual architecture. If a client-side API key cannot safely be protected in an Android app, clearly explain the risk and use the safest practical architecture available.
9. Before adding any NEW paid service, identify:
   - What service it is
   - Why it is needed
   - Whether it costs money
   - Whether usage limits apply
   - Whether a free/local alternative exists
   - Whether the existing API can perform the required function instead
10. Prefer my existing API over introducing another provider when it can reasonably perform the task.
11. Do not silently change providers because an implementation is easier.
12. If a required feature genuinely cannot be implemented with my existing API, stop at the architecture decision point and clearly state what additional service would be required rather than silently adding it.

COST SAFETY:
The application is for personal/testing use and will NOT be published to Google Play. Therefore, do not add Google Play publishing infrastructure or assume Play Store publication is required.

The project should avoid unnecessary recurring costs. Local Android functionality, offline processing, open-source libraries, and existing project capabilities should be preferred whenever they provide a reasonable solution.

AI FEATURES:
AI-powered features may use my existing API, including where appropriate:
- Rewrite
- Improve writing
- Grammar correction
- Formalize
- Simplify
- Expand
- Shorten
- Compose
- Reply generation
- Translation
- Summarization
- Explanation
- Continue writing
- Tone transformation
- Smart writing assistance
- Context-aware suggestions where technically appropriate

However:
- Do not send sensitive text unnecessarily.
- Do not send password-field content to the AI.
- Respect input-field privacy and Android editor types.
- Do not transmit clipboard contents to the AI unless the user explicitly invokes a feature that requires it.
- Do not make AI requests for every character typed.
- Give the user clear control over AI features.
- Provide graceful offline behavior.

API TESTING:
After implementing the API integration:
- Verify request construction.
- Verify authentication handling without exposing credentials.
- Verify response parsing.
- Verify malformed-response handling.
- Verify timeout behavior.
- Verify cancellation.
- Verify retry behavior.
- Verify rate-limit handling.
- Verify network-loss recovery.
- Verify empty-response handling.
- Verify unexpected API errors.
- Verify rapid repeated AI actions do not create conflicting results.
- Verify stale AI responses cannot overwrite newer user actions.
- Verify the keyboard remains usable when the API fails.

SECURITY RULE:
If I provide an API key later, treat it as a secret. Never reproduce it in generated source code, logs, documentation, screenshots, Git commits, or chat output. Tell me exactly where the secret should be configured securely instead.

FINAL RULE:
Use my existing API intelligently. Do not remove it merely because a local implementation is possible, and do not add another paid provider merely because it is convenient. Build the best architecture around the API I already have while keeping the rest of the keyboard usable without it.


============================================================
ULTIMATE FINAL META-AUDIT LAYER
============================================================

Do not treat the existing checklist as a reason to stop thinking. Before declaring the project complete, perform one final engineering audit for the following meta-level concerns.

1. REQUIREMENTS TRACEABILITY
Every requested feature must map to:
- a clear implementation;
- the correct file/module;
- relevant tests or verification;
- a documented completion state.

Do not mark a requirement complete merely because a button or placeholder exists.

2. DEPENDENCY DECISION RULES
For every external library or SDK:
- explain why it is needed;
- check whether Android/Kotlin/local code can reasonably provide the same capability;
- avoid unnecessary dependencies;
- verify compatibility and licensing;
- avoid dependencies that create unnecessary maintenance or cost.

3. ARCHITECTURE BOUNDARIES
Keep major responsibilities appropriately separated:
- UI/presentation;
- keyboard/IME layer;
- text/language intelligence;
- AI/network layer;
- local storage/database;
- settings/configuration;
- media;
- diagnostics.

Do not create tightly coupled code that becomes difficult to test or maintain.

4. BACKWARD COMPATIBILITY
Before changing an existing component:
- understand what currently depends on it;
- preserve working behavior;
- check existing features after the change;
- avoid breaking previously implemented functionality.

5. FEATURE FLAGS AND SAFE EXPERIMENTATION
Where useful, allow experimental or risky features to be disabled without breaking the keyboard.

6. ROLLBACK STRATEGY
For significant changes:
- keep the last known working state identifiable;
- avoid destructive rewrites;
- make failures recoverable;
- do not leave the repository in a half-migrated state.

7. ENVIRONMENT AND SECRET SEPARATION
Keep development/testing configuration separate from private production/personal configuration.
Never place secrets in source control, generated APK resources, logs, screenshots, or documentation.

8. DATA CONSISTENCY
Check that:
- settings;
- learned vocabulary;
- clipboard data;
- temporary workspace state;
- AI state;
- cached data
cannot easily become contradictory or corrupted.

9. PRIVACY-SAFE OBSERVABILITY
Diagnostics should make failures understandable without unnecessarily recording:
- typed text;
- passwords;
- private clipboard contents;
- private AI prompts/responses;
- sensitive personal data.

10. PERFORMANCE BUDGETS
Define and check practical limits for:
- keyboard startup;
- key-response latency;
- memory usage;
- background work;
- AI request frequency;
- animation cost;
- media/cache size;
- battery impact.

A feature that works but makes typing noticeably laggy is not acceptable.

11. ACCESSIBILITY REGRESSION TESTING
Whenever the UI changes, re-check:
- TalkBack;
- touch targets;
- text scaling;
- contrast;
- reduced-motion behavior;
- keyboard navigation where relevant;
- accessibility labels/state announcements.

12. SECURITY REGRESSION TESTING
Whenever architecture, storage, networking, or AI integration changes, re-check:
- secrets;
- exported components;
- logs;
- backups;
- local data;
- network transmission;
- permissions;
- API authentication;
- malicious or malformed input.

13. AI REGRESSION TESTING
If the AI provider, API implementation, prompts, models, or response parsing changes:
- re-test every AI action;
- verify malformed output handling;
- verify stale-response protection;
- verify cancellation;
- verify rate-limit behavior;
- verify privacy boundaries;
- verify that existing AI functions still work.

14. USER-CONTROLLED RECOVERY
Where appropriate, provide working controls for:
- resetting settings;
- restoring defaults;
- clearing learned data;
- clearing clipboard history;
- disabling AI;
- clearing caches;
- recovering from corrupted local state.

These controls must actually perform the stated operation.

15. CONFIGURATION MIGRATION
When settings/data structures change:
- migrate old versions safely;
- provide defaults for missing values;
- handle invalid old values;
- never assume every installation starts from a clean state.

16. CLEAN-BUILD VERIFICATION
Do not rely on stale build artifacts, IDE caches, or previously generated files.
Verify that the project can be built from a clean state using the actual project configuration.

17. REPOSITORY HYGIENE
Before completion, check for:
- duplicate files;
- dead implementations;
- temporary files;
- debug credentials;
- API keys;
- unnecessary generated artifacts;
- unused dependencies;
- broken references;
- conflicting implementations.

18. DOCUMENTATION ACCURACY
Documentation, comments, settings descriptions, and help text must match the actual behavior.
Do not document a feature as working when it is only a placeholder.

19. INDEPENDENT FINAL REVIEW
After implementation appears complete, perform a fresh review as if you did not write the code.
Look specifically for:
- forgotten requirements;
- incomplete states;
- fake functionality;
- broken edge cases;
- inconsistent UI;
- security weaknesses;
- performance regressions;
- accessibility problems;
- API failures;
- lifecycle bugs;
- device-specific failures.

20. FINAL COMPLETION STANDARD
Do not declare the project complete because:
- the app compiles;
- the keyboard opens;
- the main buttons work;
- the happy path works.

Completion requires:
READ → UNDERSTAND → AUDIT → PLAN → IMPLEMENT → BUILD → TEST → BREAK → FIX → RETEST → VERIFY → FINAL INDEPENDENT AUDIT.

FINAL MASTER RULE:
For every feature, determine its normal, edge, failure, cancellation, interruption, lifecycle, concurrency, privacy, security, performance, accessibility, recovery, migration, and compatibility behavior where relevant. Implement the necessary handling and verify it before declaring the feature complete.

Do not keep adding complexity merely for the sake of having more features. Prefer a smaller number of robust, integrated, tested features over a larger number of superficial or fake features.

============================================================
END OF ULTIMATE FINAL META-AUDIT LAYER
============================================================
