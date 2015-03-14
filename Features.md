# Feature Timeline #

| **Feature** | **Version** |
|:------------|:------------|
| [Basic Viewer](Features#Basic_Viewer.md) | 1.0 |
| Code Maintenance/Clean Up|1.0|
| [Selective Reader](Features#Selective_Reader.md)|2.0|
| [Interface Redone](Features#Interface_Redone.md)|2.0|
| [Article View](Features#Article_View.md)|2.0|
| [Offline Mode](Features#Offline_Mode.md)|3.0|
| [Headline Mode](Features#Headline_Mode.md)|3.1|
| [Themes/Interface Redone](Features#Theming.md)|???|
| [Domain Scripts](Features#Domain_Scripts.md)|???|
| [User Switch](Features#User_Switch.md)|XXX|

# Features Descriptions #
The following is a list of features along with descriptions.  They are in no specific order.  For a tentative order, see [Timeline](Features#Timeline.md)

### Basic Viewer ###
Create a basic viewer to view google reader items in the folder "nook", but only in oldest to newest order.  Allow items to be read/unread

### Selective Reader ###
Allow the user to select which feeds to read, including all folders.  If a folder is selected, all sub-feeds are selected, but if the user deselects a sub-item, the folder is no longer selected, and only the individual feeds are allowed.  No plans for checking to see if the feed has been unsubscribed.

### Article View ###
Develop an article view, so that if the user views an article it does not open up in the default browser.  This is important for the scripting aspect.  Additionally, it allows preloading of articles for a more seamless transition from feed to articles and between articles.

### Domain Scripts ###
Allow local scripts/css to run for domains.  For example, if I need to login to slashdot for my custom settings, allow the script to login.  If there is a link that needs to be followed to view the whole article (I'm looking at you TechCrunch), allow the script to click it before the user reads the article.

Additionally, if the article works well with readability, allow the user/script to enable that as well.

### Interface Redone ###
Find a good design/designer for the touchscreen.  Allow the user to navigate and select various options based on their preferences.

### Theming ###
Develop a theming mechanism for custom interfaces.

### User Switch ###
Integrate with [nookapp-users](http://code.google.com/p/nookapp-users/) to allow custom settings/accounts for multiple users.

## Offline Mode ##
Develop an offline mode for feeds and articles.  Allow the user to select the location of offline feeds, and determine if images are included.  Allow domain scripts for offline articles as well.

### Headline Mode ###
Allow the user to see the headlines of multiple reader items, choose which items to view, and then view only those items.