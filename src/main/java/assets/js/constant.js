var TEMPLATE_HEADER_FOOTER = "<thead><tr><th>User Story</th><th>SUMMARY</th><th>PRIORITY</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th><th>PLANNED</th><th>UNPLANNED</th></tr></thead><tfoot><tr><th>User Story</th><th>SUMMARY</th><th>PRIORITY</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th><th>PLANNED</th><th>UNPLANNED</th></tr></tfoot>";
var TEMPLATE_HEADER_FOOTER_1 = "<thead><tr><th>Assignee</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th></tr></thead><tfoot><tr><th>Assignee</th><th>UNEXECUTED</th><th>FAILED</th><th>WIP</th><th>BLOCKED</th><th>PASSED</th></tr></tfoot>";
var GREENHOPPER_ISSUE_API_LINK = 'https://greenhopper.app.alcatel-lucent.com/issues/?jql=';
var GREENHOPPER_BROWSE_ISSUE_LINK = "https://greenhopper.app.alcatel-lucent.com/browse/";
var GET_PROJECTS_URI = "/listproject";
var SAVE_GADGET_URI = "/gadget/save";
var GET_GADGETS_URI = "/gadget/gadgets";
var GET_EPIC_URI = "/getEpicLinks";
var GET_DATA_URI = "/gadget/getData";
var GET_STORY_URI = "/gadget/getStoryInEpic";
var GET_CYCLE_URI = "/listcycle";
var GET_PRODUCT_URI = "/product/getall";
var GET_ASSIGNEE_URI = "/getassignee";
var GET_EXISTING_CYCLE_URI = "/cycleExisting";
var EPIC_TYPE;
var US_TYPE;
var ASSIGNEE_TYPE;
var CYCLE_TYPE;