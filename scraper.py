from bs4 import BeautifulSoup
from typing import Any, Dict, List
from collections import Counter
import json
import re
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
import time
from selenium.webdriver import ActionChains
from selenium.webdriver.common.actions.action_builder import ActionBuilder
from selenium.webdriver.common.actions.mouse_button import MouseButton
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException

# class_dict = {code: [] for code in class_codes}
class_dict = {}

class SeleniumScraper:
    def __init__(self, user_agent='Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'):
        options = Options()
        # options.add_argument('--headless')
        options.add_argument(f'user-agent={user_agent}')
        self.driver = webdriver.Chrome(options=options)
        print("SELENIUM INSTANTIATED")

        # https://storage.googleapis.com/chrome-for-testing-public/123.0.6312.124/mac-arm64/chromedriver-mac-arm64.zip

    def scrape_text(self) -> str:
        # print(1)
        # self.driver.get(url)
        # print(2)
        self.driver.set_page_load_timeout(20)
        start_time = time.time()
        try:
            self.driver.get("https://cab.brown.edu/")
            WebDriverWait(self.driver, 10).until(lambda d: d.execute_script('return document.readyState') == 'complete')
            # time.sleep(5)
            load_time = time.time() - start_time
            print(f"Page loaded in {load_time} seconds")
        except TimeoutException:
            load_time = time.time() - start_time
            print(f"Timeout after {load_time} seconds")
            return "Page load timeout"

        # time.sleep(3)
        # print(5)

        search_bar = self.driver.find_element(By.ID, 'crit-keyword')
        search_bar.send_keys('ARAB')
        time.sleep(1)

        search_btn = self.driver.find_element(By.ID,'search-button')
        ActionChains(self.driver)\
                .click(search_btn)\
                .perform()
        
        time.sleep(1)
        class_list = self.driver.find_elements(By.CSS_SELECTOR,'div.result.result--group-start')

        for class_btn in class_list:
            try:
                class_btn.click()
                # WebDriverWait(self.driver, 10).until(
                #     lambda driver: self.content_has_changed(initial_content, driver.find_element(By.CLASS_NAME, 'detail-title').text)
                # )
                time.sleep(3)
                
                try:
                    class_code = self.driver.find_element(By.CLASS_NAME, 'dtl-course-code').text
                except NoSuchElementException:
                    class_code = "Course code not found."

                try:
                    class_title = self.driver.find_element(By.CLASS_NAME, 'detail-title').text
                except NoSuchElementException:
                    class_title = "Class title not found."

                # class_code = self.driver.find_element(By.CLASS_NAME, 'dtl-course-code')
                # class_title = self.driver.find_element(By.CLASS_NAME, 'detail-title')

                try:
                    sec_desc = self.driver.find_element(By.CSS_SELECTOR,'div.section.section--description')
                    class_desc = sec_desc.find_element(By.CLASS_NAME, 'section__content').text
                except NoSuchElementException:
                    class_desc = "Description not found."

                # sec_desc = self.driver.find_element(By.CSS_SELECTOR,'div.section.section--description')
                # class_desc = sec_desc.find_element(By.CLASS_NAME, 'section__content')
                
                try:
                    sec_reg = self.driver.find_element(By.CSS_SELECTOR,'div.section.section--registration_restrictions')
                    class_reg_restrictions = sec_reg.find_element(By.CLASS_NAME, 'section__content').text
                except NoSuchElementException:
                    class_reg_restrictions = "Registration restrictions not found/provided."

                # sec_reg = self.driver.find_element(By.CSS_SELECTOR,'div.section.section--registration_restrictions')
                # class_reg_restrictions = sec_reg.find_element(By.CLASS_NAME, 'section__content' )

                try:
                    sec_instructor_prev = self.driver.find_element(By.CSS_SELECTOR,'div.section.section--instructordetail_html')
                    sec_profs = sec_instructor_prev.find_elements(By.CLASS_NAME, 'instructor')
                    prof_arr = []
                    for prof in sec_profs:
                        prof_dict = {}
                        prof_dict["name"] = prof.find_element(By.TAG_NAME, 'a').text
                        prof_dict["email"] = prof.find_element(By.TAG_NAME, 'p').find_element(By.TAG_NAME, 'a').text
                        prof_arr.append(prof_dict)
                except NoSuchElementException:
                    prof_arr = ["Instructor(s) TBD or not provided."]

                # sec_instructor_prev = self.driver.find_element(By.CSS_SELECTOR,'div.section.section--instructordetail_html')
                # sec_profs = sec_instructor_prev.find_elements(By.CLASS_NAME, 'instructor')

                try:
                    sec_times = self.driver.find_element(By.CSS_SELECTOR,'div.section.section--all_sections')
                    sec_rows = sec_times.find_elements(By.CSS_SELECTOR,'a.course-section.course-section--matched')

                    section_arr = []
                    for section in sec_rows:
                        section_dict = {}
                        section_dict['section_num'] = section.find_element(By.CLASS_NAME, 'course-section-section').text
                        section_dict['section_crn'] = section.find_element(By.CLASS_NAME, 'course-section-crn').text
                        section_dict['section_meets'] = section.find_element(By.CLASS_NAME, 'course-section-all-sections-meets').text
                        section_dict['section_instr'] = section.find_element(By.CLASS_NAME, 'course-section-instr').text
                        section_arr.append(section_dict)
                except NoSuchElementException:
                    section_arr = ["Sections TBD or not available."]

                # print("CLASS: ",class_title,"| CODE: ", class_code)
                # print("DESC: ",class_desc)
                # print("RESTRICTIONS: ", class_reg_restrictions)
                # print("PROF ARR:", prof_arr)
                # print("SEC ARR: ", section_arr)
                
                match = re.match(r"([A-Z]+)\s", class_code)
                if match:
                    class_dept =  match.group(1).strip()
                    if class_dept not in class_dict:
                        class_dict[class_dept] = []
                else:
                    class_dept = "error"
                # print("Class dept: ", class_dept)
                # print("MEGA DICT: ", class_dict)

                return_dict = {}
                return_dict["class_dept"] = class_dept
                return_dict["class_title"] = class_title  
                return_dict["class_code"] = class_code
                return_dict["class_description"] = class_desc
                return_dict["class_reg_restrictions"] = class_reg_restrictions  
                return_dict["class_instructors"] = prof_arr 
                return_dict["class_sections"] = section_arr
                class_dict[class_dept].append(return_dict)

            except Exception as e:
                print("Error clicking some element:", e)
                continue
            
        # print(class_dict)

    def close(self):
        self.driver.quit()


sel = SeleniumScraper()

sel.scrape_text()
# print(class_dict)

json_dict = json.dumps(class_dict, indent=4)
print(json_dict)

sel.close()