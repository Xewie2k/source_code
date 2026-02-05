from playwright.sync_api import sync_playwright
import os

def verify_profile():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        try:
            print("Navigating to Profile Page...")
            page.goto("http://localhost:5173/client/account/profile")

            try:
                page.wait_for_selector("div.spinner-border", state="hidden", timeout=5000)
            except:
                print("Spinner didn't disappear")

            page.screenshot(path="profile_page.png")
            print("Screenshot taken at profile_page.png")

        except Exception as e:
            print(f"Error: {e}")
        finally:
            browser.close()

if __name__ == "__main__":
    verify_profile()
