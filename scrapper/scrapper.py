import scrapy

class Pages(scrapy.Spider):
    name = 'globo_spider'
    start_urls = ['https://globoesporte.globo.com/busca/?q=futebol&order=recent&species=not%C3%ADcias']

    def parse(self, response):
        SET_SELECTOR = '.widget--info__text-container' 
        for page in response.css(SET_SELECTOR):
            LINK_SELECTOR = '.widget--info__text-container a ::attr(href)'
            url = page.css(LINK_SELECTOR).extract_first()
            yield scrapy.Request(
                'https:' + url,
                callback = self.parse2
            )

        NEXT_PAGE_SELECTOR = '.pagination.widget a ::attr(href)'
        next_page = response.css(NEXT_PAGE_SELECTOR).extract_first()
        if next_page:
            yield scrapy.Request(
                response.urljoin(next_page),
                callback = self.parse
            )

    def parse2(self, response):
        body = str(response.body)
        i = body.find('replace("') + 9
        url = ''
        while (True):
            url += body[i]
            i += 1
            if body[i] == '"':
                break
        yield scrapy.Request(
            url=url,
            callback=self.parse_page 
        )

    def parse_page(self, response):
        article = ''

        FIRST_TEXT_SELECTOR = '.mc-column.content-text.active-extra-styles.active-capital-letter'
        TEXT_SELECTOR = '.wall'
        ROW_SELECTOR = 'div p'
        TITLE_SELECTOR = '.content-head__title ::text'

        title = response.css(TITLE_SELECTOR).extract_first()
        first_text = response.css(FIRST_TEXT_SELECTOR)
        article += first_text.css('p ::text').extract_first()
        wall = response.css(TEXT_SELECTOR)
        for text in wall.css(ROW_SELECTOR):
            row = text.css('::text').extract_first()
            if row:
                article += row

        filename = 'brasileirao/' + title + '.txt'
        with open(filename, 'w') as f:
            f.write(article)