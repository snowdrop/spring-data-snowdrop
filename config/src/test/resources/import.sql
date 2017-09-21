insert into fruit (name) values
    ('Cherry'),
    ('Apple'),
    ('Banana');

insert into simple (name, text, number, buul, hero, color, latitude, longitude) values
    ('ann', 'Does Ann like good red apples?', -20, true, 'Superman', 'red', 10.0, 10.0),
    ('barb', 'Why is Barb dancing twist?', -10, true, 'Spiderman', 'red', 24.0, 32.0),
    ('carl', 'Carl is good at running and jumping.', 0, true, 'Flash', 'red', 20.0, 20.0),
    ('doug', 'Doug likes to sleeps.', 10, false, 'Batman', 'black', -10.0, -10.0),
    ('eva', 'Eva is running in circles.', 20, false, 'Ironman', 'gold', -20.0, 5.0),
    ('fanny', 'Fanny is reading a good book.', 30, false, 'Aquaman', 'blue', 5.0, -20.0);

insert into contained (name, number) values
    ('Frank Dalton', 12),
    ('Emmett Dalton', 42),
    ('Oliver Twist', 55);

insert into simple_contained (simple_entity_id, contained_id)
    (
    select s.id, c.id
    from simple s, contained c
    where
        s.name = 'ann' and c.name = 'Frank Dalton'
        or s.name = 'ann' and c.name = 'Emmett Dalton'
        or s.name = 'eva' and c.name = 'Oliver Twist'
    );
