package filters

import akka.event.LoggingFilter

import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import play.filters.gzip.GzipFilter

class Filters @Inject() (
                            gzip: GzipFilter,
                            log: LoggingFilter
                        ) extends DefaultHttpFilters(gzip, log)
