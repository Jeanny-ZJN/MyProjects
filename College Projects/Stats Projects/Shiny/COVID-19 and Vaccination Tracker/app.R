#
# This is a Shiny web application. You can run the application by clicking
# the 'Run App' button above.
#
# Find out more about building applications with Shiny here:
#
#    http://shiny.rstudio.com/
#

library(tidyverse)
library(shiny)
library(shinythemes)
library(janitor)
library(purrr)
library(lubridate)
library(plotly)
library(DT)
library(ggthemes)
library(maptools)
library(maps)     
library(sp)       
library(maptools) 
library(mapdata)
library(ggiraph)
library(RColorBrewer)
library(viridis)
library(coronavirus)
library(broom)
library(mapproj)

# filter out Asian countries
coronavirus_trend_asia <- coronavirus %>% 
  filter(continent_name == "Asia") %>% 
  group_by(country, date) %>% 
  summarize(cases_per_100k = sum(cases)/population*100000) %>% 
  filter(cases_per_100k >= 0)

vaccination_trend_asia <- covid19_vaccine %>% 
  filter(continent_name == "Asia") %>% 
  mutate(people_at_least_one_dose = people_at_least_one_dose/population) %>% 
  select(country_region, date, people_at_least_one_dose) %>% 
  filter(between(people_at_least_one_dose, 0, 1))

# create a vector of asian countries' names
asian_countries <- coronavirus %>%
  filter(continent_name == "Asia") %>%
  select(country) %>%
  unique() %>%
  pull(country)

#map data of asian countries
asia_map <- map_data("world", asian_countries)

# Define UI for the application
ui <- bootstrapPage(
  tags$head(includeHTML("gtag.html")),
  navbarPage(
    theme = shinytheme("flatly"), collapsible = TRUE, windowTitle = "COVID-19 and Vaccination tracker", HTML('<a style="text-decoration:none;cursor:default;color:#FFFFFF;" class="active" href="#">COVID-19 and Vaccination Tracker in Asia</a>'), id="nav",
    
    tabPanel("Trend Analysis",
     sidebarLayout(
       sidebarPanel(
         selectInput(inputId = "regions",
                     label = "Which region(s) to plot?",
                     choices = asian_countries,
                     selected = c("Afghanistan"),
                     multiple = TRUE),
         sliderInput(inputId = "date_range",
                     label = "Select date:",
                     min = as.Date(min(coronavirus_trend_asia$date), "%Y-%m-%d"),
                     max = as.Date("2022-02-07"),
                     value = c(as.Date(min(coronavirus_trend_asia$date), "%Y-%m-%d"), as.Date("2022-02-07"))
                     ),
       ),
       mainPanel(
         tabsetPanel(
           tabPanel("COVID Cases", girafeOutput("covid_plot")),
           tabPanel("Vaccination Rate", girafeOutput("vacc_plot"))
         )
       )
      ),
    ),
    
    tabPanel("Cases Map",
      sidebarLayout(
        sidebarPanel(
          dateInput("map_date", label = "Select date", value = as.Date("2022-02-07"), min = min(coronavirus_trend_asia$date), max = as.Date("2022-02-07")),
          h5("For Vaccination rates, due to insufficient data, the minimum date is set as 2021-01-04")
        ),
        mainPanel(
          tabsetPanel(
            tabPanel("COVID Cases Map", girafeOutput("covid_map")),
            tabPanel("Vaccination Map", girafeOutput("vaccination_map"))
          )
        )
      )
    )
  )
)

# Define server logic for the application
server <- function(input, output) {
  
  # covid cases trendline
  output$covid_plot <- renderggiraph({
    filtered_df <- coronavirus_trend_asia %>% 
      filter(country %in% input$regions) %>% 
      filter(date >= input$date_range[1] & date <= input$date_range[2])
    
    filtered_df$tooltip <- str_c("Region = ", str_to_upper(filtered_df$country), "\n Date =", as.character(filtered_df$date), "\n Cases =", filtered_df$cases_per_100k)

    girafe_plot <- ggplot(filtered_df, aes(x = date, y = cases_per_100k)) +
      geom_line_interactive(aes(colour = country), size = 1, alpha = 0.5) +
      geom_point_interactive(aes(colour = country, tooltip = tooltip), size = 1) +
      theme_minimal() +
      theme(legend.position = "bottom",
            legend.text = element_text(size = 10),
            legend.title = element_blank(),
            axis.title.y = element_blank(),
            panel.grid.major = element_blank(), 
            panel.grid.minor = element_blank(),
            plot.margin = margin(t = 1, 0, 0, 0, "cm")) +
      ggtitle("Number of COVID-19 Cases Per 100k")
    girafe(ggobj = girafe_plot)
  })
  
  # vaccination trendline
  output$vacc_plot <- renderggiraph({
    filtered_df <- vaccination_trend_asia%>% 
      filter(country_region %in% input$regions) %>% 
      filter(date >= input$date_range[1] & date <= input$date_range[2])
    
    filtered_df$tooltip <- str_c("Region = ", str_to_upper(filtered_df$country_region), "\n Date =", as.character(filtered_df$date), "\n Vaccination Rate =", filtered_df$people_at_least_one_dose)
    
    girafe_plot <- ggplot(filtered_df, aes(x = date, y = people_at_least_one_dose)) +
      geom_line_interactive(aes(colour = country_region), size = 1, alpha = 0.5) +
      geom_point_interactive(aes(colour = country_region, tooltip = tooltip), size = 1) +
      theme_minimal() +
      theme(legend.position = "bottom", 
            legend.text = element_text(size = 10),
            legend.title = element_blank(),
            axis.title.y = element_blank(),
            panel.grid.major = element_blank(), 
            panel.grid.minor = element_blank(),
            plot.margin = margin(t = 1, 0, 0, 0, "cm")) +
      ggtitle("Vaccination Rate")
    girafe(ggobj = girafe_plot)
  })
  
  #code for map
  output$covid_map <-renderGirafe({
    
      #filter the data for the specified date and join with map data
      asia_covid <- coronavirus_trend_asia %>%
        filter(date == input$map_date) %>% 
        left_join(asia_map, by = c("country" = "region"))
      asia_covid$tooltip <- str_c("Country = ", str_to_upper(asia_covid$country), "\n Cases per 100k =", asia_covid$cases_per_100k)
      
      map <- asia_covid %>%
        select(country, cases_per_100k, long, lat, group, tooltip) %>%
        na.omit() %>%
        ggplot(aes(long, lat, group = group)) + coord_map() + theme_map() +
        geom_polygon_interactive(aes(fill = cases_per_100k,
                                     tooltip = tooltip, 
                                     data_id = cases_per_100k)) +
        guides(fill=guide_legend(title="Cases per 100k")) + 
        scale_fill_gradientn(colours = terrain.colors(10)) + 
        theme(legend.position = "right") +
        ggtitle("Map based on COVID cases")
      
      ggiraph(code = print(map),  hover_css = "fill:lightgreen;r:10pt;")
      
     
    
  })
  
  output$vaccination_map <- renderGirafe({
    
    use_date <- input$map_date
    
    if(use_date < "2021-01-04"){
      use_date <- "2021-01-04"
    }
    
    #filter the data for the specified date and join with map data
      asia_vaccination <- vaccination_trend_asia %>%
        filter(date == use_date) %>% 
        left_join(asia_map, by = c("country_region" = "region"))
      
      asia_vaccination$tooltip <- str_c("Country = ", str_to_upper(asia_vaccination$country_region), "\n % Fully Vaccinated =", asia_vaccination$people_at_least_one_dose)
      
      map <- asia_vaccination %>%
        select(country_region, people_at_least_one_dose, long, lat, group, tooltip) %>%
        na.omit() %>%
        ggplot(aes(long, lat, group = group)) + coord_map() + theme_map() +
        geom_polygon_interactive(aes(fill = people_at_least_one_dose,
                                     tooltip = tooltip, 
                                     data_id = people_at_least_one_dose)) +
        guides(fill=guide_legend(title="Percent of Fully Vaccination")) + 
        scale_fill_gradientn(colours = terrain.colors(10)) + 
        theme(legend.position = "right") +
        ggtitle("Map based of Vaccination rates")
      
      ggiraph(code = print(map),  hover_css = "fill:lightgreen;r:10pt;")
    
  })
}

# Run the application 
shinyApp(ui = ui, server = server)
