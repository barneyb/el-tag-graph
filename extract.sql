select e.event
     , e.timestamp
     , exp(ln(0.5) *
           (unix_timestamp('2019-01-15T08:00:00') - unix_timestamp(timestamp)) /
           (86400 * 28)) weight
     , tag -- concat('t', t.id) tag
from (
         select e.id
              , concat('e', e.id) event
              , e.timestamp
         from event e
         where e.userId = 5
           and e.timestamp between '2019-01-01' and '2019-01-15'
           and not exists(
             select 1
             from eventtag
                      join tag on eventtag.tagId = tag.id
             where eventId = e.id
               and tag like '-%'
             )
     ) e
         join eventtag et on e.id = et.eventId
         join tag t on et.tagId = t.id
where et.explicit
  and t.tag not like '-%'
order by 2, 3;
