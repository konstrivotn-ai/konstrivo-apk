# PLACO Montant Counting Policy

Purpose
-------
This document records the current meaning, formulas and policy alternatives used to count vertical montants (studs) for PLACO / cloison calculations. It is intentionally non-normative: it documents existing code behavior and the decisions required before changing production calculations.

Context (golden case)
---------------------
- Wall length: 7.3 m
- Wall height: 3.8 m
- Entraxe (stud spacing): 0.40 m
- Stud positions (code): floor(length / entraxe) + 1 = 19 positions
- Montant stock length (catalog): 3.0 m
- Height allowance factor (code): tallCoupling = 1.1 when height > 3.0
- Effective height per position = height × tallCoupling = 3.8 × 1.1 = 4.18 m

Definitions
-----------
- Stud positions: the discrete vertical locations where a stud (montant) is required along the wall. Computed as `floor(wallLength / entraxe) + 1` (includes both ends).
- Stock length: the nominal length of a single purchased profile piece (meters). For `montant_48` this is 3.0 m.
- Splice / Rallonge: the method of joining two or more stock pieces vertically to reach the required position height. The repo guidance recommends 30–40 cm overlap and fixing with 4 TRPF screws.
- Height allowance: the code applies a multiplicative allowance `tallCoupling` = 1.1 for heights > 3.0 m (≈ +10%) to account for splices and waste.

Two counting policies (explicit)
-------------------------------
1) GLOBAL_LINEAR_OPTIMIZED (current-like, but consistent with stock length)
   - Formula: `purchasedPieces = ceil((positions × effectiveHeight) / stockLength)`
   - Interpretation: aggregate the total linear meters required across all positions, then divide by stock length and round up. Maximizes material yield / minimizes number of purchased pieces.
   - Example (golden case, stock=3.0m): `ceil(19 × 4.18 / 3.0) = ceil(79.42 / 3.0) = 27` pieces.

2) PER_POSITION_CONSERVATIVE (per-position guarantee)
   - Formula: `purchasedPieces = positions × ceil(effectiveHeight / stockLength)`
   - Interpretation: ensure each stud position has independently-allocated stock pieces to reach required height (no reliance on inter-position cutting). Safer for site logistics, may increase purchased quantity.
   - Example (golden case, stock=3.0m): `19 × ceil(4.18 / 3.0) = 19 × 2 = 38` pieces.

Legacy / Production behavior (observed)
--------------------------------------
- The repository contains a legacy production path that uses `params.boardLengthM` (default 2.5 m) as the divisor in some montants calculations. That path yields a production result of 32 pieces for the golden case: `ceil(19 × 4.18 / 2.5) = 32`.
- Tests in the codebase assert both `piecesFor3m = 27` (if divisor 3.0) and `piecesFor2_5m = 32` (if divisor 2.5) demonstrating both variants exist in the project.

The field value `36` (observed) — why unresolved
-------------------------------------------------
- The field reference '36 Montants' does not match any single, canonical result from the documented formulas:
  - Global, stock=3.0 → 27
  - Global, divisor=2.5 → 32 (current production path)
  - Per-position, stock=3.0 → 38
- 36 could be produced by ad-hoc adjustments (for example, `production(32) + safetyBuffer(4)`), or by an optimized cutting yield that is neither strictly global nor strictly per-position (for example: some positions require only 1 piece, others 2, after cutting optimization). There is no code, test, or documentation in the repo that codifies any rule that yields exactly 36.

Comparison (golden case)
------------------------
- Stud positions: 19
- Effective height per position: 4.18 m
- Stock length (montant_48): 3.0 m

Counting policy results (golden case):
- Global (stock=3.0): 27 purchased pieces
- Global (legacy divisor=2.5): 32 purchased pieces (current production observed)
- Per-position (stock=3.0): 38 purchased pieces
- Field reference: 36 (no derivation in code/tests)

Recommended governance decision (required before changing production)
-------------------------------------------------------------------
One of the following must be chosen and documented as a data-driven policy before any production code change that would make `36` reproducible:

1. Adopt PER_POSITION_CONSERVATIVE (safer). This will change production to 38 for the golden case and guarantees each position can be built without cross-position cutting.

2. Adopt GLOBAL_LINEAR_OPTIMIZED with an explicit configurable safety buffer. Example rule: `purchased = ceil(totalLinear / stock) + safetyBufferPieces` where `safetyBufferPieces` is a configurable integer (default 0). If the desired field is 36, that corresponds to `production(32) + safetyBuffer(4)` for the current legacy divisor case.

3. Standardize the divisor to `montantStockLengthM = 3.0` everywhere (remove use of `boardLengthM` for montants calculations) and then choose either (1) or (2). This will make the base production result 27 (global) or 38 (per-position), removing ambiguity from mixing 2.5 and 3.0 divisors.

Required new data fields (if adopting data-driven safety)
---------------------------------------------------------
- `montantStockLengthM` (double) — explicit stock length for montants (3.0 m)
- `montantCountingPolicy` (enum: GLOBAL_LINEAR_OPTIMIZED | PER_POSITION_CONSERVATIVE)
- `montantSafetyBufferPieces` (int) — optional additional pieces to add after calculation
- `montantSpliceOverlapM` (double) — recommended default 0.35 m (30–40 cm) for use in verification and per-position piece breakdown

Next steps (non-invasive)
-------------------------
- Record the team decision: choose one of the recommended governance options. Document the chosen policy in this file and then create a follow-up implementation PR that is limited and data-driven.
- Do not change production calculations until the policy and required fields are agreed and captured.

Document history
----------------
Created to capture the current ambiguity around a field marked '36 Montants' and to recommend an explicit policy.

---
Author: automation (developer documentation)
Location: docs/PLACO_MONTANT_POLICY.md
