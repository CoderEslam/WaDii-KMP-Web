# WaDii UAT Report: Analysis

## 1. Executive Summary

The app looks clean and has the main pages for all three roles, but **the core business loop does not work**:

> Buyer requests a part → Providers send proposals → Buyer accepts → Deal is closed

It breaks at four points:

1. **Routing.** A quote request to one seller goes to every seller (C-01).
2. **Notifications.** Neither buyers nor providers are told anything happened (C-04, C-05).
3. **Closing.** The buyer cannot accept, reject or negotiate a proposal (C-03).
4. **Visibility.** The admin dashboard is blank, so nobody can measure deals (E-01).

On top of this, input validation and file-upload security are weak, which is a risk before real users join.

### Findings by severity (35 total)

| Severity | Count | Share |
|---|---|---|
| Critical | 2 | 6% |
| High | 6 | 17% |
| Medium | 17 | 49% |
| Low | 10 | 29% |

I re-counted every finding ID in the report. The totals match the summary table.

### Findings by area

| Area | Findings | Critical | High | Medium | Low |
|---|---|---|---|---|---|
| A. Sign-up, Account, Security | 4 | 1 | 1 | 1 | 1 |
| B. Navigation and Layout | 5 | 0 | 0 | 3 | 2 |
| C. Order and Quote Flow | 11 | 1 | 4 | 6 | 0 |
| D. Provider Pages | 6 | 0 | 0 | 4 | 2 |
| E. Admin Panel | 8 | 0 | 1 | 4 | 3 |
| F. Test Data and Content | 1 | 0 | 0 | 0 | 1 |

**Section C is the most important.** It holds 11 of 35 findings, 5 of the 8 Critical and High items, and the whole revenue path.

---

## 2. Blockers: Critical and High Items

| ID | Issue | Why it matters |
|---|---|---|
| **C-01** (Critical) | "Request Part Quote" to a specific seller creates a general order visible to all providers. The chosen seller gets no link, no pending entry and no notification. | Breaks the main promise of the feature. Also leaks the buyer's request to competitors. |
| **A-02** (Critical) | Seller application accepts any file type for ID and tax card uploads, and the phone field accepts text. | Security risk: malicious file uploads, injection, and exposure of sensitive identity documents. |
| **C-03** (High) | Buyer can only read a proposal. No accept, reject, counter-offer or chat. | No deal can ever be closed, so the platform cannot measure or earn from completed deals. |
| **C-04** (High) | Buyer gets no notification when a provider replies. | Buyers must keep checking My Orders. Response time and engagement suffer. |
| **C-05** (High) | Provider has 5 incoming orders but an empty Notifications page. | Providers miss leads and stop engaging. |
| **A-01** (High) | Sign-up has no validation. Name takes any characters, email is unverified, phone is unchecked. | Fake accounts, bad data and spam. |
| **C-02** (High) | Submitting an order without choosing a service shows "Failed to create order" with no reason. | Users cannot tell what to fix and abandon the form. |
| **E-01** (High) | Admin Dashboard is completely blank. | The business owner cannot see users, orders, deals or pending seller requests. |

---

## 3. Root-Cause Themes

Grouping the 35 findings by cause is more useful than reading them by screen. Fixing a theme once closes several findings.

### Theme 1: Missing core transaction logic
**C-01, C-03, C-06, C-09, C-11**
Orders are not tied to specific sellers, proposals have no buyer actions, and there is no status lifecycle. These are product gaps, not bugs. They need design decisions (for example, what states exist and who can move an order between them).

### Theme 2: No notification and status layer
**C-04, C-05, C-07, C-11, D-01**
The system does not tell users when something changed. It also does not show where each order stands (replied, awaiting buyer, accepted). One shared status and event model would fix most of this.

### Theme 3: Weak validation and security
**A-01, A-02, A-03, A-04, C-02, E-03, D-05**
Forms accept anything. The most serious cases are file uploads (A-02) and free-text ad links shown to every user (A-03). Fixes should be **server-side**, as the report says, not only on the front end.

### Theme 4: Admin has no visibility or control
**E-01, E-05, E-06, E-07, E-08**
The admin cannot see KPIs, cannot manage users, cannot monitor orders or deals, cannot suspend accounts, and loses the history of seller requests.

### Theme 5: UX and navigation gaps
**B-01 to B-05, C-10, D-02, D-03, D-06, E-02, E-04**
There is no header or footer, search is on a separate empty page, "Switch to Seller" is hard to find, icons are generic, and lists lack sorting and filters.

### Theme 6: Content and localisation
**C-08, F-01, D-04**
Prices are in USD for an Egyptian market, test data is visible to users, and expired deals still show as active.

---

## 4. Dependencies Between Fixes

Some fixes should be done in a set order.

```
C-11 Order statuses ──┬─► C-03 Accept / Reject / Negotiate
                      ├─► C-07 Provider status tabs
                      ├─► D-01 Meaningful "Pending" number
                      └─► E-01 Admin KPIs (accepted deals, conversion rate)

C-01 Direct request routing ──► C-05 Provider notifications (direct requests)

C-03 Proposal actions ──┬─► C-04 Buyer notification on proposal
                        ├─► C-06 Reasons used on reject / cancel
                        └─► C-09 Chat / phone after proposal

A-01 Sign-up validation ──► A-02 Seller application validation (same rules reused)
```

**Practical consequence:** define the order status model (C-11) early. Several Critical and High items, plus the admin KPIs, depend on it.

---

## 5. Observations on the Report Itself

These are points where I read the report critically.

1. **The "Fix first" list in the summary leaves out A-01 and C-02.** Both are High and both are in Phase 1 of the fix plan. The summary and the plan should match.
2. **A-03 (malicious ad links) is rated Medium, but it is a security issue.** Any admin-entered external link is shown to every user on the Home page. It is lower risk than A-02 because only admins can create ads, but it should be fixed with Phase 1 validation work. A compromised admin account would make it serious.
3. **C-08 (USD pricing) is rated Medium, but it affects trust.** It is a quick fix and a user-visible one, and it is likely easy to bundle into Phase 1.
4. **C-09 (buyer phone visible to provider) needs a policy decision.** The report suggests showing the phone only after the provider sends a proposal. That affects privacy and spam, so it should be agreed with the product owner first.
5. **E-08 is marked "To verify", not confirmed.** Check what happens when a used category or location is deleted before treating it as a bug.
6. **The test was exploratory and covered one buyer, one seller and one admin.** The report does not mention performance, mobile or responsive behaviour, browser coverage, or accessibility. These are untested, not passed.
7. **The report is mostly functional.** The Critical and High security items (A-02, A-01) are described as validation issues. A proper security review (authentication, authorisation between roles, file storage) would be a sensible next step.

---

## 6. Recommended Plan

The report proposes three phases. I agree with them and suggest one adjustment.

### Phase 1: Before any real users
**Scope:** C-01, A-02, A-01, C-02, C-03, C-04, C-05, E-01
**Goal:** A buyer can request, receive, accept and close a deal safely, and the admin can see it.

**Suggested adjustments:**
- Start with the **status model (C-11)**, because other items depend on it.
- Add **A-03** (ad link validation) and **C-08** (EGP pricing) to this phase.

### Phase 2: Before public launch
**Scope:** All Medium items, plus F-01 (clean test data).
**Goal:** Complete statuses, filters, EGP pricing, admin management pages, and a clean live environment.

**Also:** set up a **separate test environment** (F-01). That prevents test data from reaching production again.

### Phase 3: After launch
**Scope:** Remaining Low items (polish and quality of life).

### Retest script after each phase
Run with **one buyer, two providers and one admin**:

1. Request a quote from a specific seller.
2. Send a proposal.
3. Negotiate.
4. Accept.
5. Cancel with a reason.
6. Check notifications and the admin dashboard at every step.

---

## 7. Full Findings Register

### A. Sign-up, Account and Security

| ID | Screen | Issue | Severity |
|---|---|---|---|
| A-01 | Sign-up | No validation on name, email (no OTP) or phone | High |
| A-02 | Profile › Switch to Seller | No validation, unrestricted ID/tax-card uploads | **Critical** |
| A-03 | Admin › New Ad | Image and target URLs are unchecked free text | Medium |
| A-04 | Admin › Providers | Provider links accept any text | Low |

### B. Navigation and Layout

| ID | Screen | Issue | Severity |
|---|---|---|---|
| B-01 | All pages | Side panel only, no header or footer | Medium |
| B-02 | Home / Search | Search on a separate page that opens empty | Medium |
| B-03 | Profile | "Switch to Seller" is small and easy to miss | Medium |
| B-04 | Home | Floating "+" button has no label | Low |
| B-05 | Side menu | Generic icons, duplicate Sign Out | Low |

### C. Order and Quote Flow

| ID | Screen | Issue | Severity |
|---|---|---|---|
| C-01 | Seller card › Request Part Quote | Direct request becomes a general order seen by all | **Critical** |
| C-02 | Request Part Quote / New Order | Generic "Failed to create order" error | High |
| C-03 | Buyer › My Orders | No accept, reject or negotiate on proposals | High |
| C-04 | Buyer notifications | No notification on new proposal | High |
| C-05 | Provider notifications | Empty despite 5 incoming orders | High |
| C-06 | Admin › Reasons | Cancel reasons not used in the buyer flow | Medium |
| C-07 | Provider › Orders | No status after sending a proposal | Medium |
| C-08 | Provider › Proposal | Prices in USD | Medium |
| C-09 | Provider › Order details | No chat or buyer phone | Medium |
| C-10 | Buyer › New Order | Thin form: free-text vehicle, no photos, city or condition | Medium |
| C-11 | Orders | No visible lifecycle status | Medium |

### D. Provider Pages

| ID | Screen | Issue | Severity |
|---|---|---|---|
| D-01 | Provider › Dashboard | "Pending" card shows a word, not a number | Medium |
| D-02 | Provider › Recent Orders | Sorted oldest first | Medium |
| D-03 | Provider › Incoming Orders | No filters or search | Medium |
| D-04 | My Deals / Buyer Home | Expired deals still shown as active | Medium |
| D-05 | My Deals | Duplicate deals allowed | Low |
| D-06 | Seller cards / Profile | New sellers show 0.0 stars | Low |

### E. Admin Panel

| ID | Screen | Issue | Severity |
|---|---|---|---|
| E-01 | Admin › Dashboard | Blank dashboard | High |
| E-02 | Admin › New Ad | Empty red error box on load | Medium |
| E-03 | Admin › Ads | No date rules (active 1992 to 2068), unclear Priority | Medium |
| E-04 | Admin › New Ad | Advertiser name not linked to a provider | Low |
| E-05 | Admin menu | No users, orders or deals pages, no suspend option | Medium |
| E-06 | Admin › Seller Requests | No history of approved or rejected requests | Low |
| E-07 | Admin › Providers | No search or filter, addresses not linked to Locations | Low |
| E-08 | Admin › Categories / Locations | To verify: deleting items that are in use | Low |

### F. Test Data and Content

| ID | Screen | Issue | Severity |
|---|---|---|---|
| F-01 | Home, Ads, Providers, Locations | Placeholder data visible ("Title", "Del 2", "El Boob", "test", "Spain") | Low |

---

## 8. Screenshot Evidence (Appendix)

| Figure | Shows | Related findings |
|---|---|---|
| Fig. 1 | Blank Admin Dashboard | E-01 |
| Fig. 2 | Buyer Home with placeholder ad, test seller and expired deals | D-04, F-01 |
| Fig. 3 | New Ad form with empty red error box | E-02 |
| Fig. 4 | Ads list with an ad active 1992 to 2068 | E-03 |
| Fig. 5 | Provider Dashboard with "Pending" as a word | D-01, D-02 |
| Fig. 6 | Profile with small "Switch to Seller" button | B-03 |

---

## 9. Bottom Line

WaDii has a good visual foundation, but it is **a catalogue of pages, not yet a working marketplace**. The sequence that matters is:

1. Define order statuses.
2. Route direct requests correctly and notify both sides.
3. Let buyers accept, reject and negotiate.
4. Lock down validation and uploads.
5. Give the admin a working dashboard.

Once those five things work end to end, the remaining 27 findings are mostly polish and management tooling.
