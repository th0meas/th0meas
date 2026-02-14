[[uni]] #SQL
pgadmin4: pgadmin.iee.ihu.gr
username: andrthom
password: andreasb


## Relational Algebra

### **Core Operations**
**σ (Selection)** - Filters rows (like Where in SQl)
- `σ_{condition}(Relation)`
- Example: `σ_{age > 30}(Employees)` = all employees older than 30
**π (Projection)** - Picks specific columns
- `π_{column1, column2}(Relation)`
- Example: `π_{name, salary}(Employees)` = only name and salary columns
**∪ (Union)** – Combines two relations (removes duplicates)
- `A ∪ B` returns tuples in A or B or both
**− (Difference)** – Finds tuples in one relation but not the other
- `A − B` = in A but not in B
**× (Cartesian Product)** – Combines every tuple of one relation with every tuple of another
- Used as the base for joins
**⨝ (Join)** – Combines tuples from two relations based on a condition
- `R ⨝ S` (natural join or theta join)
**ρ (Rename)** – Renames a relation or its attributes
-  `ρ_{new_name}(Relation)`

examples here: [[raexamples]]
