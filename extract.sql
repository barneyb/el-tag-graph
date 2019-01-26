with events as (
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
),
     tagged_events as (
         select e.event
              , e.timestamp
              , t.id              tag_id
              , concat('t', t.id) tag
         from events e
                  join eventtag et on e.id = et.eventId
                  join tag t on et.tagId = t.id
         where et.explicit
           and t.tag not like '-%'
         order by 2, 4
     )
     ( -- events
         select event
              , null              tag
              , 1                 "use"
              , exp(ln(0.5) * (unix_timestamp('2019-01-15T08:00:00') -
                               unix_timestamp(timestamp)) /
                    (86400 * 28)) recent_use
         from events
     )
union
( -- tags
    select null
         , tag
         , count(*)
         , sum(exp(ln(0.5) * (unix_timestamp('2019-01-15T08:00:00') -
                              unix_timestamp(timestamp)) /
                   (86400 * 28)))
    from tagged_events
    group by tag_id, tag
)
union
( -- uses
    select event
         , tag
         , 1
         , exp(ln(0.5) * (unix_timestamp('2019-01-15T08:00:00') -
                          unix_timestamp(timestamp)) /
               (86400 * 28))
    from tagged_events
)
order by case
             when tag is null then 1
             when event is null then 2
             else 3
             end, 1, 2;