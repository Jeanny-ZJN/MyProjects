# import libraries
library(shiny)
library(shinyWidgets)
library(shinythemes)
library(bslib)
library(tidyverse)
library(plotly)
library(broom)
library(ggthemes)
library(factoextra)
library(tidymodels)
library(gtsummary)
library(gt)
library(rpart.plot)
library(vip)
library(plyr)
library(shinycustomloader)
library(lazyeval)
library(RColorBrewer)
library(ranger)

# load data sets
alcohol_consumption<-read_csv("./datasets/alcohol-consumption-cleaned.csv")
gdp<-read_csv("./datasets/gdp-per-capita-cleaned.csv")
happiness_index<-read_csv("./datasets/happiness-cleaned.csv")
homicide<-read_csv("./datasets/homicide-rate-cleaned.csv")
literacy_rates<-read_csv("./datasets/literacy-rates-cleaned.csv")
workhours<-read_csv("./datasets/working-hours-cleaned.csv")
corruption<-read_csv("./datasets/corruption-perception-index-cleaned.csv")
urban<-read_csv("./datasets/urban-population-cleaned.csv")

# data set options
datasets2 <- c("Alcohol Consumption", "Corruption Perception Index", "GDP Per Capita", "Life Satisfaction", "Homicide Rate", "Literacy Rate", "Urban Population", "Working Hours")

# mapping dataset
df <- read.csv("https://raw.githubusercontent.com/plotly/datasets/master/2014_world_gdp_with_codes.csv")
df2<-df %>% dplyr::rename(region = "COUNTRY")

# standardization function
standardize <- function(x, na.rm = FALSE) {
  (x - mean(x, na.rm = na.rm)) / sd(x, na.rm = na.rm)
}

# generate cluster labels
kmeans_ac <- alcohol_consumption[-1] %>% mutate(across(alcohol_consumption, standardize))
kmeans.final <- kmeans(kmeans_ac[,3], 3, nstart = 25)
kmeans_augment <- augment(kmeans.final, data = kmeans_ac)

# rename cluster
kmeans_level <- kmeans_augment %>% 
  group_by(.cluster) %>% 
  mutate(
    level = case_when(
      .cluster==1 ~ "Low Alcohol Consumption",
      .cluster==2 ~ "Median Alcohol Consumption",
      .cluster==3 ~ "High Alcohol Consumption")
  )

# Define UI logic
ui <- fluidPage(
  navbarPage(
    theme = bs_theme(version = 4, bootswatch = "minty"),
    collapsible = TRUE,
    HTML('<a style="text-decoration:none;cursor:default;color:#F3969A;" class="active" href="#">Alcohol Consumption Around the World</a>'),
    id="nav",
    windowTitle = "Alcohol Consumption Around the World",
    tags$head(
      tags$style(type="text/css", "#inline label{ display: table-cell; text-align: left; vertical-align: middle; }
                #inline .form-group { display: table-row;}")
    ),
    
    tabPanel("Introduction",
             fluidRow(
               column(3),
               column(6,
                      shiny::HTML("<br><br><center> <h2>Alcohol has played a central role in almost all human cultures since Neolithic times. </h2> </center><br>"),
                      shiny::HTML("<h6> Regardless of cultural origin, all civilization make use of intoxicating substances, alcohol being by far the most common. 
                                  However, in modern society, we hypothesize that the quantity of alcohol consumption is linked to many social, economic, and cultural factors.</h6>"),
                      shiny::HTML("<br>"),
                      shiny::HTML("<h6> On our website, we would like to investigate the topic of alcohol consumption in more depth. This entry looks at global distributions of alcohol consumption
                                  as well as its trends over time. We also provide the opportunity to compare socioeconomic and cultural factors such as GDP, the Happiness Index, literacy rate and homicide rate
                                  to depict more comprehensive outlooks on how alcohol consumption of individuals in each country may be affected. </h6>"),
                      shiny::HTML("<br>"),
                      shiny::HTML("<h6> Social drinking or moderate alcohol consumption for many is pleasurable.
                      However, alcohol consumption - especially in excess - is linked to a number 
                      of negative outcomes: as a risk factor for diseases and health impacts; crime; road 
                      incidents; and for some, alcohol dependence. Globally alcohol consumption causes 2.8 million premature deaths per year [1].</h6>"),
                      shiny::HTML("<br>"),
                      shiny::HTML("<h6> Datasets used for our analysis were sourced from website 'Our World in Data', a website with data sets of all major human activities throughout history maintained by researchers from University of Oxford.
                                  Details of the data sets used for our study are described below.</h6>"),
                      tags$ul(
                        tags$li(h6("Alcohol Consumption: annual average alcohol consumption of alcohol, expressed per person aged 15 years or older [1].")),
                        tags$li(h6("Happiness Index: measurement of average self-reported life satisfaction.")),
                        tags$li(h6("Working Hours: annual average working hours per individual [2].")),
                        tags$li(h6("Corruption: measurement of abuse of entrusted power for private gain in each country [3].")),
                        tags$li(h6("GDP: measures overall economic propsperity and development.")),
                        tags$li(h6("Literacy Rate: measures overall educational level within a country, indicator of social development.")),
                        tags$li(h6("Homicide: measurement of interpersonal violence that ultimately caused death [4].")),
                        tags$li(h6("Urbanization: measurement of overall industrialization, indicator of economic development "))),
                      shiny::HTML("<br>"),
                      shiny::HTML("<h6>Resources Used for our project:</h6>"),
                      tags$ol(
                        tags$li(h6("https://ourworldindata.org/alcohol-consumption")),
                        tags$li(h6("https://ourworldindata.org/working-hours")),
                        tags$li(h6("https://ourworldindata.org/corruption")),
                        tags$li(h6("https://ourworldindata.org/homicides"))
                      ),
                      shiny::HTML("<br><br>")
               ),
               column(3)
             )
    ),
    
    
    tabPanel("Overview", #Tab for the plot and table
             sidebarLayout(
               sidebarPanel(
                 selectInput("dataset1", "
                             Select Dataset:", 
                             choices = datasets2, 
                             selected = "Alcohol Consumption"),
                 uiOutput("nations")
               ),
               mainPanel(
                 tabsetPanel(
                   tabPanel("Overall Trends", plotlyOutput("lineplot")),
                   tabPanel("Map", plotlyOutput("mapplot"))))
             )
    ),
    
    tabPanel("Explore Factors", #Tab for exploring relationships of alcohol consumption and other factors
             sidebarLayout(
               sidebarPanel(
                 selectInput(inputId = "x",
                             label = "x:",
                             choices = datasets2[-1],
                             selected = datasets2[2]),
                 selectInput(inputId = "y",
                             label = "y:", 
                             choices = "Alcohol Consumption", 
                             selected = "Alcohol Consumption"),
                 uiOutput("slider"),
                 radioButtons(inputId = "log", 
                              label = "Log x?",
                              choices = c("yes", "no"),
                              selected = "no"),
                 radioButtons(inputId = "label", 
                              label = "Label Points?",
                              choices = c("yes", "no"),
                              selected = "no")
               ),
               mainPanel(
                 tabsetPanel(
                   tabPanel("Scatterplot", withLoader(plotlyOutput("scatterplot"), type="html", loader="loader1")),
                   tabPanel("Simple Linear Regression", 
                            withLoader(plotlyOutput("regression_plot"), type="html", loader="loader1"),
                            br(),
                            br(),
                            withLoader(gt_output("regression_table"), type="html", loader="loader1")),
                   tabPanel("K-Means Clustering", 
                            plotOutput("elbowplot"),
                            uiOutput("kmeans_text"),
                            tags$div(id = "inline", uiOutput("select_k")),
                            withLoader(plotOutput("kmeans"), type="html", loader="loader1"))
                 )
               )
             )
             
    ),
    
    tabPanel("Decision Trees",
             sidebarLayout(
               sidebarPanel(
                 selectInput(inputId = "variables",
                             label = "Select one or more datasets:",
                             choices = datasets2[-1],
                             selected = c("Corruption Perception Index", "GDP Per Capita", "Life Satisfaction", "Literacy Rate", "Working Hours"),
                             multiple = TRUE),
                 actionButton("render_dt", "Run decision trees", width = "180px"),
                 br(),
                 br(),
                 actionButton("render_rf", "Run random forest", width = "180px"),
                 br(),
                 br(),
                 helpText("Decision trees will show up under Decision Trees panel. Variable importance will show up under Random Forest panel.",
                          "Please do NOT click button(s) multiple times. It will just slow down the rendering.")
               ),
               mainPanel(
                 tabsetPanel(
                   tabPanel("Methodology", uiOutput("methodology")),
                   tabPanel("Decision Trees Plot", plotOutput("tree")),
                   tabPanel("Random Forest Plot", plotOutput("importance"))
                 )
               )
             )
    )
    
  )
)

# Define server logic
server <- function(input, output) {
  
  dataset1 <- reactive({
    if (input$dataset1 == "Alcohol Consumption"){
      return(list(data = alcohol_consumption, 
                  variable_name = "alcohol_consumption",
                  y_axis = "Alcohol Consumption (litres per capita)")) 
    }
    
    if (input$dataset1 == "Corruption Perception Index"){
      return(list(data = corruption, 
                  variable_name = "corruption_perception_index", 
                  y_axis = "Corruption Perception Index")) 
    }
    if (input$dataset1 == "GDP Per Capita"){
      return(list(data = gdp, 
                  variable_name = "gdp", 
                  y_axis = "GDP Per Capita ($))"))
    }
    if (input$dataset1 == "Life Satisfaction"){
      return(list(data = happiness_index, 
                  variable_name = "happiness", 
                  y_axis = "Self-reported Life Satisfaction Score"))
    }
    if (input$dataset1 == "Homicide Rate"){
      return(list(data = homicide, 
                  variable_name = "homicide_rate", 
                  y_axis = "Number of Homicide Deaths per 100,000 People"))
    }
    if (input$dataset1 == "Literacy Rate"){
      return(list(data = literacy_rates, 
                  variable_name = "literacy_rates", 
                  y_axis = "Literacy Rate (above age 14)"))
    }
    if (input$dataset1 == "Urban Population"){
      return(list(data = urban, 
                  variable_name = "urban_population", 
                  y_axis = "Share of Population Living in Urban Areas"))
    }
    if (input$dataset1 == "Working Hours"){
      return(list(data = workhours, 
                  variable_name = "working_hours", 
                  y_axis = "Annual Working Hours Per Worker"))
    }
  })
  
  # tab: Overview
  output$nations <- renderUI({
    dataset <- dataset1()$data
    selectInput(inputId = "choose_nations",
                label = "Select one or more region(s):",
                choices = unique(dataset$region),
                selected = NULL,
                multiple = TRUE)
  })
  
  output$lineplot <- renderPlotly({
    if (length(input$choose_nations) < 1) {
      dataset1()$data %>% 
        group_by_(~year, ~continent) %>% 
        summarize_(mean = interp(~mean(x, na.rm = TRUE), x = rlang::sym(dataset1()$variable_name)), continent = ~continent, year = ~year) %>% 
        ungroup() %>% 
        plot_ly(x = ~year, y = ~mean, type = "scatter", mode = 'lines+markers', color = ~continent) %>% 
        layout(yaxis = list(title = dataset1()$y_axis))
    } else {
      dataset1()$data %>% 
        filter(region %in% input$choose_nations) %>% 
        plot_ly(x = ~year, y = as.formula(str_c("~", dataset1()$variable_name)), type = "scatter", mode = 'lines+markers', color = ~region) %>% 
        layout(yaxis = list(title = dataset1()$y_axis))
    }
  })
  
  output$mapplot <-renderPlotly({
    joined_map_df <- left_join(alcohol_consumption, df2, "region" = "region")
    
    l <- list(color = toRGB("grey"), width = 0.5)
    g <- list(
      showframe = FALSE,
      showcoastlines = FALSE,
      projection = list(type = 'Mercator')
    )
    
    fig1 <- plot_geo(joined_map_df)
    
    fig2 <-  fig1 %>% add_trace(
      z = ~alcohol_consumption, color = ~alcohol_consumption, colors = colorRampPalette(brewer.pal(8,"Pastel2"))(7),
      text = ~region, locations = ~CODE, marker = list(line = l)) %>% 
      layout(title = "Annual Average Alcohol Consumption Distribution")
    fig2
  })
  
  dataset2 <- reactive({
    if (input$x == "Corruption Perception Index"){
      return(corruption)
    }
    if (input$x == "GDP Per Capita"){
      return(gdp)
    }
    if (input$x == "Life Satisfaction"){
      return(happiness_index)
    }
    if (input$x == "Homicide Rate"){
      return(homicide)
    }
    if (input$x == "Literacy Rate"){
      return(literacy_rates)
    }
    if (input$x == "Urban Population"){
      return(urban)
    }
    if (input$x == "Working Hours"){
      return(workhours)
    }
  })
  
  log <- reactive({
    return(input$log)
  })
  
  x <- reactive({
    if (input$x == "Corruption Perception Index"){
      return("corruption_perception_index")
    }
    if (input$x == "GDP Per Capita"){
      return("gdp")
    }
    if (input$x == "Life Satisfaction"){
      return("happiness")
    }
    if (input$x == "Homicide Rate"){
      return("homicide_rate")
    }
    if (input$x == "Literacy Rate"){
      return("literacy_rates")
    }
    if (input$x == "Urban Population"){
      return("urban_population")
    }
    if (input$x == "Working Hours"){
      return("working_hours")
    }
  })
  
  axis <- reactive({
    if (input$x == "Corruption Perception Index"){
      return("Corruption Perception Index")
    }
    if (input$x == "GDP Per Capita"){
      return("GDP Per Capita ($)")
    }
    if (input$x == "Life Satisfaction"){
      return("Self-reported Life Satisfaction Score")
    }
    if (input$x == "Homicide Rate"){
      return("Number of Homicide Deaths per 100,000 People")
    }
    if (input$x == "Literacy Rate"){
      return("Literacy Rate (above age 14)")
    }
    if (input$x == "Urban Population"){
      return("Share of Population Living in Urban Areas")
    }
    if (input$x == "Working Hours"){
      return("Annual Working Hours Per Worker")
    }
  })
  
  label <- reactive({
    return(input$label)
  })
  
  decision_tree_df <- reactive({
    df_ls <- list()
    for (df in input$variables) {
      if (df == "Corruption Perception Index"){
        df_ls$corruption <- get("corruption")[-1]
      }
      if (df == "GDP Per Capita"){
        df_ls$gdp <- get("gdp")[-1]
      }
      if (df == "Life Satisfaction"){
        df_ls$happiness_index <- get("happiness_index")[-1]
      }
      if (df == "Homicide Rate"){
        df_ls$homicide <- get("homicide")[-1]
      }
      if (df == "Literacy Rate"){
        df_ls$literacy_rates <- get("literacy_rates")[-1]
      }
      if (df == "Urban Population"){
        df_ls$urban <- get("urban")[-1]
      }
      if (df == "Working Hours"){
        df_ls$workhours <- get("workhours")[-1]
      }
    }
    
    db_df <- join_all(df_ls, type = "inner", by = c("region", "year", "continent"))
    return(db_df)
  })
  
  output$slider <- renderUI({
    joined_df <- inner_join(alcohol_consumption, dataset2(), by = c("region", "year", "continent"))
    sliderTextInput(inputId = "slider", 
                    label = "Year:", 
                    choices = levels(as.factor(joined_df$year)),
                    grid = TRUE,
                    selected = max(joined_df$year)
    )
  })
  
  output$kmeans_text <- renderUI({
    url <- a("elbow method", href="https://en.wikipedia.org/wiki/Elbow_method_(clustering)")
    tagList("The optimal k for k-means clustering can be found by looking at the plot above using the ", url, ".")
  })
  
  output$select_k <- renderUI({
    label <- HTML(paste(
      p(strong("Visualize clustering with k = "), HTML('&nbsp;'))))
    autonumericInput(inputId = "k",
                     label = label,
                     value = 3,
                     width = "10px",
                     align = "left",
                     decimalPlaces = 0,
                     maximumValue = 10,
                     minimumValue = 1,
                     modifyValueOnWheel = FALSE)
  })
  
  # tab: Explore Factors
  output$scatterplot <- renderPlotly({
    
    # join data sets for scatterplot
    joined_df <- inner_join(alcohol_consumption, dataset2(), by = c("region", "year", "continent"))
    joined_df <- filter(joined_df, year == input$slider)
    
    # determine log or no
    if (log() == "yes"){
      x_var <- as.formula(str_c("~log10(", x(), ")"))
    } else {
      x_var <- as.formula(str_c("~", x()))
    }
    
    # plot scatterplot with or without labels
    if(label() == "yes"){
      plot_ly(data = joined_df, x = x_var, y = ~alcohol_consumption, color = ~continent, text = joined_df$region, mode = "marker+text") %>% 
        add_markers() %>% 
        add_text(textfont = list(family = "sans serif",
                                 size = 10,
                                 color = toRGB("grey50")), 
                 textposition = "top right", 
                 showlegend = FALSE) %>% 
        layout(xaxis = list(title = axis()),
               yaxis = list(title = 'Alcohol Consumption (litres per capita)'))
    } else {
      plot_ly(data = joined_df, x = x_var, y = ~alcohol_consumption, color = ~continent, mode = "marker") %>% 
        add_markers() %>% 
        layout(xaxis = list(title = axis()),
               yaxis = list(title = 'Alcohol Consumption (litres per capita)'))
    }
  })
  
  output$elbowplot <- renderPlot({
    set.seed(233)
    
    # format data set for elbow plot
    joined_df <- inner_join(alcohol_consumption, dataset2(), by = c("region", "year", "continent"))
    joined_df <- filter(joined_df, year == input$slider)
    joined_df <- select(joined_df, -continent, -year)
    
    # check if need to log
    if (log() == "yes"){
      joined_df[,5] <- log10(joined_df[,5])
    }
    
    joined_df <- joined_df %>% mutate(across(where(is.numeric), standardize))
    joined_df <- joined_df[c(colnames(joined_df)[2], colnames(joined_df)[5], colnames(joined_df)[3])]
    
    # generate elbow graph
    multi_kmeans <- tibble(k = 1:10) %>%
      mutate(
        model = purrr::map(k, ~kmeans(joined_df[,2:3], centers = .x, nstart = 25)),
        tot.withinss = purrr::map_dbl(model, ~ glance(.x)$tot.withinss)
      )
    
    multi_kmeans %>%
      ggplot(aes(k, tot.withinss)) +
      geom_point() +
      geom_line() +
      scale_x_continuous(breaks = 1:15) +
      theme_minimal() +
      ggtitle("Elbow Plot")
  })
  
  output$kmeans <- renderPlot({
    set.seed(233)
    
    # format data set for cluster plot
    joined_df <- inner_join(alcohol_consumption, dataset2(), by = c("region", "year", "continent"))
    joined_df <- filter(joined_df, year == input$slider)
    joined_df <- select(joined_df, -continent, -year)
    
    # check if need to log
    if (log() == "yes"){
      joined_df[,5] <- log10(joined_df[,5])
    }
    
    joined_df <- joined_df %>% mutate(across(where(is.numeric), standardize))
    joined_df <- joined_df[c(colnames(joined_df)[2], colnames(joined_df)[5], colnames(joined_df)[3])]
    
    # kmeans clustering with selected k
    kmeans.final <- kmeans(joined_df[,2:3], input$k, nstart = 25)
    joined_df <- as.data.frame(joined_df)
    rownames(joined_df) <- joined_df$region
    
    # cluster plot
    fviz_cluster(kmeans.final, data = joined_df[,2:3], labelsize = 10, show.clust.cent = TRUE, repel = TRUE, ggtheme =  theme_tufte())
  })
  
  output$regression_plot <- renderPlotly({
    
    # join data sets for simple linear regression
    joined_df <- inner_join(alcohol_consumption, dataset2(), by = c("region", "year", "continent"))
    joined_df <- filter(joined_df, year == input$slider)
    joined_df <- select(joined_df, -continent, -year)
    
    # check if need to log
    if (log() == "yes"){
      x_var <- as.formula(str_c("~log10(", x(), ")"))
    } else {
      x_var <- as.formula(str_c("~", x()))
    }
    
    joined_df <- joined_df %>% mutate(across(where(is.numeric), standardize))
    joined_df <- joined_df[c(colnames(joined_df)[2], colnames(joined_df)[5], colnames(joined_df)[3])]
    
    # model fitting
    formula <- as.formula(str_c("alcohol_consumption ~ ", x()))
    lm_model <- linear_reg() %>% 
      set_engine('lm') %>% 
      set_mode('regression') %>%
      fit(formula, data = joined_df) 
    
    y <- joined_df$alcohol_consumption
    X <- joined_df[,2]
    
    x_range <- seq(min(X), max(X), length.out = 100)
    x_range <- matrix(x_range, nrow=100, ncol=1)
    xdf <- data.frame(x_range)
    colnames(xdf) <- c(x())
    ydf <- lm_model %>% predict(xdf) 
    colnames(ydf) <- c('alcohol_consumption')
    xy <- data.frame(xdf, ydf) 
    
    # plot regression line
    plot_ly(joined_df, x = x_var, y = ~alcohol_consumption, type = 'scatter', alpha = 0.65, mode = 'markers', name = 'Values') %>% 
      add_trace(data = xy, x = x_var, y = ~alcohol_consumption, name = 'Regression Fit', mode = 'lines', alpha = 1)
  })
  
  output$regression_table <- render_gt({
    
    # join data sets for simple linear regression
    joined_df <- inner_join(alcohol_consumption, dataset2(), by = c("region", "year", "continent"))
    joined_df <- filter(joined_df, year == input$slider)
    joined_df <- select(joined_df, -continent, -year)
    
    # check if need to log
    if (log() == "yes"){
      joined_df[,3] <- log10(joined_df[,3])
    }
    
    joined_df <- joined_df %>% mutate(across(where(is.numeric), standardize))
    joined_df <- joined_df[c(colnames(joined_df)[2], colnames(joined_df)[5], colnames(joined_df)[3])]
    
    # model fitting
    formula <- as.formula(str_c("alcohol_consumption ~ ", x()))
    lm_model <- linear_reg() %>% 
      set_engine('lm') %>% 
      set_mode('regression') %>%
      fit(formula, data = joined_df) 
    
    # generate summary table
    tbl_regression(lm_model) %>% as_gt()
  })
  
  # tab: Decision Trees
  output$methodology <- renderUI({
    dt_url <- a("decision trees", href="https://en.wikipedia.org/wiki/Decision_tree_learning")
    rf_url <- a("random forest", href="https://en.wikipedia.org/wiki/Random_forest")
    s1 <- tagList("This page takes in the selected variables by users and uses the ", 
                  dt_url, 
                  " algorithm to construct a decision tree, representing how these variables impact alcohol consumption level. The page also uses the ",
                  rf_url,
                  " algorithm to calculate each variable's level of importance in terms of its influence on alcohol consumption level. More importance means more influence.")
    s2 <- "Since alcohol consumption is a quantitative variable, we first categorize each data point using kmeans clustering. Based on the elbow method, we found that the optimal value for k is three. The three clusters are then renamed to 'Low Alcohol Consumption', 'Median Alcohol Consumption', and 'High Alcohol Consumption'. This column of categorical variable is then used for classification in the decision trees algorithm and the random forest algorithm."
    HTML(paste(" ", s1, s2, sep = '<br/><br/>'))
  })
  
  observeEvent(input$render_dt, {
    output$tree <- renderPlot({
      withProgress(
        message = 'Model building in progress',
        detail = 'This may take around 10 seconds...', value = 1, {
          set.seed(233)
          
          # create data sets for decision tree
          rf_df <- kmeans_level %>% inner_join(decision_tree_df(), by = c("region", "year", "continent")) %>% 
            dplyr::select(-year, -continent, -region, -alcohol_consumption)
          rf_df <- rf_df[-1]
          # create train/test data sets
          rf_split <- initial_split(rf_df, prop = 0.80, strata = level)
          rf_train <- rf_split %>% training()
          rf_test <- rf_split %>% testing()
          # create recipe
          rf_recipe <- recipe(level ~ ., data = rf_train) %>%
            step_dummy(all_nominal(), -all_outcomes()) %>%
            prep()
          # model specification
          tree_model <- decision_tree(cost_complexity = tune(),
                                      tree_depth = tune(),
                                      min_n = tune()) %>% 
            set_engine('rpart') %>% 
            set_mode('classification')
          # workflow
          tree_workflow <- workflow() %>% 
            add_model(tree_model) %>% 
            add_recipe(rf_recipe)
          # hyperparameter tuning
          tree_grid <- grid_regular(cost_complexity(),
                                    tree_depth(),
                                    min_n(), 
                                    levels = 2)
          # Create folds for cross validation on the training data set
          rf_folds <- vfold_cv(rf_train, v = 5, strata = level)
          
          incProgress(2/3, message = "Tuning decision trees")
          
          # tune decision tree workflow
          tree_tuning <- tree_workflow %>% 
            tune_grid(resamples = rf_folds,
                      grid = tree_grid)
          # select best model based on 'roc_auc'
          best_tree <- tree_tuning %>% select_best(metric = 'roc_auc')
          # finalize workflow
          final_tree_workflow <- tree_workflow %>% finalize_workflow(best_tree)
          # fit the model
          tree_wf_fit <- final_tree_workflow %>% fit(data = rf_train)
          # extract fit
          tree_fit <- tree_wf_fit %>% extract_fit_parsnip()
          
          incProgress(3/3, message = "Plotting")
          Sys.sleep(1)
          
          # plot decision tree
          rpart.plot(tree_fit$fit, main = "Decision Tree", roundint = FALSE, varlen = 0)
        })
    })
  })
  
  observeEvent(input$render_rf, {
    output$importance <- renderPlot({
      withProgress(
        message = 'Calculation in progress',
        detail = 'This may take around 30 seconds...', value = 1, {
          set.seed(233)
          
          # create data sets for decision tree
          rf_df <- kmeans_level %>% inner_join(decision_tree_df(), by = c("region", "year", "continent")) %>%
            dplyr::select(-year, -continent, -region, -alcohol_consumption)
          rf_df <- rf_df[-1]
          # create train/test data sets
          rf_split <- initial_split(rf_df, prop = 0.80, strata = level)
          rf_train <- rf_split %>% training()
          rf_test <- rf_split %>% testing()
          # create recipe
          rf_recipe <- recipe(level ~ ., data = rf_train) %>%
            step_dummy(all_nominal(), -all_outcomes()) %>%
            prep()
          # model specification
          rf_model <- rand_forest(mtry = tune(),
                                  trees = tune(),
                                  min_n = tune()) %>%
            set_engine('ranger', importance = "impurity") %>%
            set_mode('classification')
          # workflow
          rf_workflow <- workflow() %>%
            add_model(rf_model) %>%
            add_recipe(rf_recipe)
          # Create folds for cross validation on the training data set
          rf_folds <- vfold_cv(rf_train, v = 5, strata = level)
          # hyperparameter tuning
          rf_grid <- grid_random(mtry() %>% range_set(c(2,3)),
                                 trees(),
                                 min_n(),
                                 size = 15)
          
          incProgress(2/3, message = "Tuning random forest")
          
          rf_tuning <- rf_workflow %>%
            tune_grid(resamples = rf_folds, grid = rf_grid)
          # Select best model based on roc_auc
          best_rf <- rf_tuning %>% select_best(metric = 'roc_auc')
          final_rf_workflow <- rf_workflow %>% finalize_workflow(best_rf)
          rf_wf_fit <- final_rf_workflow %>% fit(data = rf_train)
          rf_fit <- rf_wf_fit %>% extract_fit_parsnip()
          
          # plot variable importance
          vip(rf_fit) + theme_minimal() + ggtitle("Variable Importance")
        })
    })
  })
  
}

# Run the application 
shinyApp(ui = ui, server = server)
