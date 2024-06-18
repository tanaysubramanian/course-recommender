# Project Details
## Project Name
CourseConnect

## Project Description
CourseConnect is intended to be a program that will assist Brown students in finding courses that they would like to take. As current Brown students, we were motivated by the frustration we often face when it comes to selecting courses that we find interesting. As such, we wanted to include features such as a keyword search, weekday and time slot selections, and the ability to add previously taken courses, all while being able to easily (and accessibly) navigate the website. 

## Team Members + Contributions
1.Tanay Subramanian
    - similarity algorithm
    - back-end
    - back-end testing
    - code organization
2. Habram Alcantar
    - front-end
    - integration with back-end
    - front-end testing
    - similarity algorithm additions
3. Anna Le
    - front-end
    - integration with back-end
    - javadocs
    - similarity algorithm additions
4. Ben Kang (bhkang)
    - mocking for back-end
    - selenium scraper for CAB
    - data pipeline to firestore
    - pytest dual-scraper validation testing

# Design Choices
We modeled most of our design choices after Burntout@Brown because it was one of the main inspirations for this project. We added an autogenerated dropdown for the department & course selections using Firestore to make it easy to fetch the data from CAB. We realized we were going to have to scrape for this project because we wanted to use large chunks of each course's information. The scraped data was arranged to be injected into a dictionary with a specific data structure that would be easy to convert into JSON and to select via department, for easy access from the front-end. The other features of the website include checkboxes for days of the week and times the class is held, the ability to add a previously taken course, the choice to prioritize a past instructor, and keyword searching. This gives the user flexibility in how they wish to filter for courses. 

We also provided four buttons for the user - submit, clear history, add course, and clear courses. These buttons are intended to help clean up the querying process and make it neater. The clear history button should empty the page, and clear courses should return the past courses to an empty state. We added some keyboard shortcuts for accessibility. These include "Enter" for submitting, "CTRL" + "SHIFT" for search box focus, arrow scrolling, and tooltip textboxes.

We chose to display the table to include the course description, restrictions, meeting times, and similarity score for the user to reference. Our job is to match what the user inputs and suggest potential courses of interest based on the similarity algorithm. Since this is a fairly new program, we cannot say how accurate or useful it is, but it functions well for the main uses we have tried out. We also chose to only scrape data for courses offered during the Fall 2024 semester. If this website were to be further developed and successful, we would update the data per semester change, which itself would not be too much work as the existing architecture is already there - only a few filepaths would need to be modified.

# Tests
## Front-end Tests
Within the front-end, we split our testing into two suites: one focused on the visibility of components and one focused on the "recommendation" functionality.

### Visibility Tests
Considering that our website contains multiple features to aid in the narrowing down of courses (i.e. day checkboxes, time checkboxes, course dropdowns, department dropdowns, and a search bar), we had to ensure that all of these items would be visible to the user. Thus, within this test suite, we ensured that all of these components would be visible. 

### Functionality Tests
Outside of the visibility of certain components within our website, we were also concerned with the ability of our back-end to accurately return recommendations and for them to be correctly formatted. As such, we created tests that ensure our project can handle: 
+ A search based on only a keyword parameter
+ A search based on only a day of the week
+ A search based on only a time of day
+ A search based on only a past course
+ A search based on multiple past courses
+ A search based on a keyword, followed by clearing the course recommendations table
+ Multiple successive keyword search queries
+ A search based on a keyword, a past course, a time of day, and a day of the week
+ Matching the exact course description to a recommended course within the recommendation table
+ A search based on professor weight
+ A search prioritizing course department weight
+ A search with a keyword with no occurrences

## Scraper Tests
The targeted scraper (the one used by the other parts of the project) functions by targeting specific divs within each course page on CAB and extracts them. However, not all courses have consistent sections and are missing others, so this testing focuses on verifying that the sections which _do_ exist _have_ been properly scraped.

Because the main scrape of CAB consists of 1255 courses (23000+ lines of JSON), this task is far too difficult to verify manually or with cross-referencing via other targeted scrapers (circular issue). Instead, we developed a general testing scraper that extracts _all_ text within a page, which will serve as our closest proxy to ground truth/accuracy from CAB, and compares to see if:
+ all course codes in the general testing scraper have been targeted by the main scraper
+ all course names in the general testing scraper have been targeted by the main scraper
+ all course descriptions in the general testing scraper have been targeted by the main scraper
+ all registration registrictions in the general testing scraper have been targeted by the main scraper
+ all ignored course descriptions within the main scraper _do not_ exist in the overall text of the general testing scraper
+ all ignored registration restrictions within the main scraper _do not_ exist in the overall text of the general testing scraper

# How To...
## Run Tests
### Running Front-end Tests
In order to run our front-end tests (assuming you have not built and run our program), you can perform the following steps: 
1. Open a terminal, and navigate to the back-end directory
2. Within the terminal, type "mvn package" and click 'Enter'
3. Within the teminal, type "./run" and click 'Enter'
4. Open another terminal, and navigate to the front-end directory
5. Within the terminal, type "npx playwright test"

In order to run our front-end tests (assuming you have built and run our program), you can perform the following steps: 
1. Open a terminal, and navigate to the front-end directory
2. Within the terminal, type "npx playwright test"

### Running Scraper Tests
1. Open the project via the scraper folder (instead of the overall folder, this is to prevent dependency issues)
2. Run `pip install -r requirements.txt` to install requisite dependencies
3. Run `pytest scrape_testing.py` to run the testing suite
4. Use `scrape_testing.py` to mess around with the specific datasets used for testing via the filepaths specified at the top; the `panel_body.json` files within the `data` directory are the scrapes from the general testing scraper, while the `sample.json` and `full_cab_...json` files are scrapes from the main scraper. 

### Running Back-end Tests
1. Clone the project and open the folder via IntelliJ
2. Access the TestSimilarity class
3. Click the green play button to run all of the tests

## Build and Run Our Program
In order to build and run our program, we must run both the front-end component and the back-end component. These can be done in any order. 
### Build and Run our Front-end
1. Open a terminal, and navigate to the front-end directory
2. Within the terminal, type "npm start" and click 'Enter'

### Build and Run Our Back-end
1. Open a terminal, and navigate to the back-end directory
2. Within the terminal, type "mvn package" and click 'Enter'
3. Within the terminal, type "./run" and click 'Enter'

### How to Update and Run the Scraper for Future Data Collection
The scraper doesn't need to run again as all data has been compiled; however, if you'd like to get updated data for future semesters, you can do the following:
1. Open the project via the scraper folder (instead of the overall folder, this is to prevent dependency issues)
2. Run `pip install -r requirements.txt` to install requisite dependencies
3. Obtain the `firebase_config.json` file to obtain read/write permissions to Firestore, or set up your own and update throughout the rest of the project
4. Navigate to [cab.brown.edu](cab.brown.edu) and inspect element on the semester selector and obtain the correct value for the semester whose courses you would like to scrape
5. Navigate to `scraper.py` and replace line 60's value with the selector value: `select_semester.select_by_value('REPLACE THIS')`
6. At the bottom of `scraper.py`, replace the call of `scrape_text()` on line 190 with a blank string, `""`, to scrape all courses within that given semester.
