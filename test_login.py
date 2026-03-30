from playwright.sync_api import sync_playwright
import json

def test_login():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context()
        page = context.new_page()

        print("1. 导航到登录页面...")
        page.goto('http://localhost:3000/login')
        page.wait_for_load_state('networkidle')

        page.screenshot(path='e:/project/shop/login_page.png', full_page=True)
        print("   已截图: login_page.png")

        print("2. 填写登录表单...")
        page.fill('#username', 'admin')
        page.fill('#password', 'admin123')

        page.screenshot(path='e:/project/shop/login_filled.png', full_page=True)
        print("   已截图: login_filled.png")

        print("3. 点击登录按钮...")
        page.click('button[type="submit"]')

        page.wait_for_load_state('networkidle')
        page.wait_for_timeout(2000)

        page.screenshot(path='e:/project/shop/login_result.png', full_page=True)
        print("   已截图: login_result.png")

        cookies = context.cookies()
        token_cookie = next((c for c in cookies if c['name'] == 'token'), None)

        if token_cookie:
            print(f"\n✅ 登录成功！Token: {token_cookie['value'][:50]}...")
        else:
            print("\n❌ 登录失败：未找到 token cookie")
            console_logs = []
            page.on("console", lambda msg: console_logs.append(msg.text))
            print(f"   控制台日志: {console_logs}")

        current_url = page.url
        print(f"   当前URL: {current_url}")

        browser.close()

if __name__ == "__main__":
    test_login()
